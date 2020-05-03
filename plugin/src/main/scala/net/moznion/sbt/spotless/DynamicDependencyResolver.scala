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

import java.io._
import java.nio.file.Files

import net.moznion.sbt.spotless.config.{SpotlessConfig, SpotlessPathConfig}

import scala.util.matching.Regex

private object DynamicDependencyResolver {
  private val mavenCoordinateRegex: Regex = "^(.+):(.+):(.+)$".r

  private def mavenCoordToModuleIDLeaves(mavenCoord: String): (String, String, String) = {
    val m = mavenCoordinateRegex.findAllIn(mavenCoord)
    m.hasNext
    (m.group(1), m.group(2), m.group(3))
  }
}

private class DynamicDependencyResolver(
    private val config: SpotlessConfig,
    private val pathConfig: SpotlessPathConfig,
    private val dependencyResolver: DependencyResolver,
    private val logger: Logger
) {
  private var cache: Map[String, Option[Seq[File]]] = Map()
  private val cacheRepository: DynamicDependencyCacheRepository =
    new DynamicDependencyCacheRepository(logger)

  Files.createDirectories(pathConfig.dynamicDependencyWorkingDir.toPath)
  Files.createDirectories(pathConfig.dynamicDependencyCacheDir.toPath)

  private[sbt] def resolve(withTransitives: Boolean, mavenCoord: String): Seq[File] = {
    cache.get(mavenCoord) match {
      case Some(cached: Option[Seq[File]]) =>
        logger.debug("hit in-memory cache")
        cached.getOrElse(Seq())
      case None =>
        // lookup the file cache
        val cacheFile = new File(pathConfig.dynamicDependencyCacheDir, mavenCoord)
        val fileCached: Option[Seq[File]] =
          if (cacheFile.exists() && !config.disableDynamicDependencyCache) {
            cacheRepository.loadCache(cacheFile) match {
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
            dependencyResolver
              .retrieve(org, name, rev, withTransitives, pathConfig.dynamicDependencyWorkingDir)
          val resolved: Option[Seq[File]] = retrieved match {
            case Right(retrieveFiles) => Option(retrieveFiles)
            case Left(err) =>
              logger.warn(err.getLocalizedMessage)
              Option.empty
          }

          if (!config.disableDynamicDependencyCache) {
            cache += (mavenCoord -> resolved)
            cacheRepository.saveCache(cacheFile, resolved)
          }

          resolved.getOrElse(Seq())
        })())
    }
  }
}
