/*
 * Copyright 2020 moznion.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.moznion.sbt.spotless

import java.io.File
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Files, Path, Paths, StandardOpenOption}

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import net.moznion.sbt.spotless.config.{SpotlessConfig, SpotlessPathConfig}

import scala.util.matching.Regex

private object DynamicDependencyResolver {
  private val charEncoding: Charset = StandardCharsets.UTF_8
  private val mavenCoordinateRegex: Regex = "^(.+):(.+):(.+)$".r

  private def mavenCoordToModuleIDLeaves(mavenCoord: String): (String, String, String) = {
    val m = mavenCoordinateRegex.findAllIn(mavenCoord)
    (m.group(1), m.group(2), m.group(3))
  }
}

private class DynamicDependencyResolver(
    private val config: SpotlessConfig,
    private val pathConfig: SpotlessPathConfig,
    private val dependencyResolver: DependencyResolver,
    private val logger: Logger,
) {
  private var cache: Map[String, Option[Seq[File]]] = Map()

  Files.createDirectories(pathConfig.dynamicDependencyWorkingDir.toPath)
  Files.createDirectories(pathConfig.dynamicDependencyCacheDir.toPath)

  private[sbt] def resolve(mavenCoord: String): Seq[File] = {
    cache.get(mavenCoord) match {
      case Some(cached: Option[Seq[File]]) =>
        logger.debug("hit in-memory cache")
        cached.getOrElse(Seq())
      case None =>
        // lookup the file cache
        val cacheFile = new File(pathConfig.dynamicDependencyCacheDir, mavenCoord)
        val fileCached: Option[Seq[File]] =
          if (cacheFile.exists() && !config.disableDynamicDependencyCache) {
            resolveFileCache(cacheFile.toPath) match {
              case Right(files) => Option(files)
              case Left(_)      => Option.empty
            }
          } else {
            Option.empty
          }

        fileCached.getOrElse((() => {
          val (org, name, rev) = DynamicDependencyResolver.mavenCoordToModuleIDLeaves(mavenCoord)
          logger.debug(s"no cache. retrieving module ID: $org:$name:$rev")

          val retrieved =
            dependencyResolver.retrieve(org, name, rev, pathConfig.dynamicDependencyWorkingDir)
          val resolved: Option[Seq[File]] = retrieved match {
            case Right(retrieveFiles) => Option(retrieveFiles)
            case Left(err) =>
              logger.warn(err.getLocalizedMessage)
              Option.empty
          }

          if (!config.disableDynamicDependencyCache) {
            cache += (mavenCoord -> resolved)
            Files.write(
              Paths.get(cacheFile.toURI),
              DependencyCache(resolved.map(r => r.map(f => f.toString))).asJson.noSpaces
                .getBytes(DynamicDependencyResolver.charEncoding),
              StandardOpenOption.CREATE,
              StandardOpenOption.WRITE,
              StandardOpenOption.TRUNCATE_EXISTING,
            )
          }

          resolved.getOrElse(Seq())
        })())
    }
  }

  private def resolveFileCache(cacheFilePath: Path): Either[Unit, Seq[File]] = {
    decode[DependencyCache](
      new String(Files.readAllBytes(cacheFilePath), DynamicDependencyResolver.charEncoding),
    ) match {
      case Right(cached) =>
        cached.dependencyPaths match {
          case Some(dependencyPaths) =>
            val depFiles = dependencyPaths.map(path => new File(path))
            if (depFiles.forall(file => file.exists())) {
              logger.debug("cached jar: use that")
              return Right(depFiles)
            }
            logger.warn("there are no jar files that are caching; it will resolve the dependencies")
            Left(Unit)
          case None =>
            logger.debug("cached jar: empty")
            Right(Seq())
        }
      case Left(err) =>
        logger.warn("failed to decode cache file; it will resolve the dependencies; " + err)
        Left(Unit)
    }
  }
}
