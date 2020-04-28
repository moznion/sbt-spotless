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
import java.util

import com.diffplug.spotless.Provisioner
import net.moznion.sbt.spotless.config.{SpotlessConfig, SpotlessPathConfig}

import _root_.scala.collection.JavaConverters._

/**
  * SbtProvisioner is a provisioner for the Spotless with sbt.
  *
  * This provisioner resolves dynamic dependency for a code formatter on-demand and on-the-fly.
  */
private[sbt] object SbtProvisioner {
  def supplyProvisioner(
      spotlessConfig: SpotlessConfig,
      pathConfig: SpotlessPathConfig,
      staticDeps: Seq[File],
      dependencyResolver: DependencyResolver,
      logger: Logger
  ): Provisioner = {
    new Provisioner {
      override def provisionWithTransitives(
          withTransitives: Boolean,
          mavenCoordinates: util.Collection[String]
      ): util.Set[File] = {
        val dynamicDependencyResolver =
          new DynamicDependencyResolver(spotlessConfig, pathConfig, dependencyResolver, logger)
        val dynamicDeps: Iterable[File] = if (spotlessConfig.disableDynamicDependencyResolving) {
          Seq()
        } else {
          mavenCoordinates.asScala.flatMap(mavenCoord => {
            logger.debug("given maven-coord: " + mavenCoord)
            dynamicDependencyResolver.resolve(mavenCoord)
          })
        }
        (staticDeps ++ dynamicDeps).toSet.asJava
      }
    }
  }
}
