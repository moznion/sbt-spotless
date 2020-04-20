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

package net.moznion.sbt

import java.io.File
import java.util

import com.diffplug.spotless.Provisioner
import net.moznion.sbt.spotless.config.{SpotlessConfig, SpotlessPathConfig}
import sbt.util.Logger

import scala.collection.JavaConverters._

/**
  * SbtProvisioner is a provisioner for the Spotless with sbt.
  */
object SbtProvisioner {
  def supplyProvisioner: (SpotlessConfig, SpotlessPathConfig, Seq[File], Logger) => Provisioner = {
    (
        spotlessConfig: SpotlessConfig,
        pathConfig: SpotlessPathConfig,
        staticDeps: Seq[File],
        logger: Logger,
    ) => (withTransitives: Boolean, mavenCoords: util.Collection[String]) =>
      {
        val dynamicDependencyResolver =
          new DynamicDependencyResolver(spotlessConfig, pathConfig, logger)
        val dynamicDeps: Iterable[File] = if (spotlessConfig.disableDynamicDependencyResolving) {
          Seq()
        } else {
          mavenCoords.asScala.flatMap(mavenCoord => {
            logger.debug("given maven-coord: " + mavenCoord)
            dynamicDependencyResolver.resolve(mavenCoord)
          })
        }
        (staticDeps ++ dynamicDeps).toSet.asJava
      }
  }
}
