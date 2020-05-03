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
import java.nio.file.{Files, Paths, StandardOpenOption}
import java.util.Base64

import net.moznion.sbt.spotless.DependencyCache.DependencyPaths

class DynamicDependencyCacheRepository(private val logger: Logger) {
  def saveCache(cacheFile: File, resolved: Option[Seq[File]]): Unit = {
    val byteStream = new ByteArrayOutputStream()
    val serialized: Array[Byte] =
      try {
        val objectStream = new ObjectOutputStream(byteStream)
        try {
          val dependencyPaths: DependencyPaths = resolved.map(r => r.map(f => f.toString))
          objectStream.writeObject(dependencyPaths)
        } finally {
          objectStream.close()
        }
        Base64.getEncoder.encode(byteStream.toByteArray)
      } finally {
        byteStream.close()
      }

    Files.write(
      Paths.get(cacheFile.toURI),
      serialized,
      StandardOpenOption.CREATE,
      StandardOpenOption.WRITE,
      StandardOpenOption.TRUNCATE_EXISTING
    )
  }

  def loadCache(cacheFile: File): Either[Unit, Seq[File]] = {
    val deserialized = Base64.getDecoder.decode(Files.readAllBytes(cacheFile.toPath))
    val objectInputStream = new ObjectInputStream(new ByteArrayInputStream(deserialized))
    val cached =
      try {
        objectInputStream.readObject().asInstanceOf[DependencyPaths]
      } finally {
        objectInputStream.close()
      }

    cached match {
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
  }
}
