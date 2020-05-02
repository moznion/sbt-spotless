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

package net.moznion.sbt.spotless.task

import java.io.File

import com.diffplug.spotless.Provisioner
import com.diffplug.spotless.cpp.CppDefaults
import com.diffplug.spotless.extra.cpp.EclipseCdtFormatterStep
import net.moznion.sbt.spotless.config.{CppConfig, SpotlessPathConfig}
import net.moznion.sbt.spotless.util.GlobResolver
import net.moznion.sbt.spotless.{FormatterSteps, Logger, RunningMode}

import _root_.scala.collection.JavaConverters._

private[sbt] case class Cpp[T <: CppConfig](
    private val config: T,
    private val pathConfig: SpotlessPathConfig,
    private val logger: Logger
) extends RunnableTask[T] {
  def run(provisioner: Provisioner, mode: RunningMode): Unit = {
    if (!config.enabled) {
      return
    }

    var steps = FormatterSteps()

    steps = Option(config.licenseHeader)
      .map(licenseHeader => steps.addStep(licenseHeader.createStep))
      .getOrElse(steps)

    steps = Option(config.licenseHeaderFile)
      .map(licenseHeaderFile => steps.addStep(licenseHeaderFile.createStep))
      .getOrElse(steps)

    steps = Option(config.eclipseCpp)
      .map(eclipseCppFormat => {
        val version = Option(eclipseCppFormat.version)
          .getOrElse(EclipseCdtFormatterStep.defaultVersion())

        val builder = EclipseCdtFormatterStep.createBuilder(provisioner)
        builder.setVersion(version)

        Option(eclipseCppFormat.configFiles)
          .foreach(configFiles => builder.setPreferences(configFiles.asJava))

        steps.addStep(builder.build())
      })
      .getOrElse(steps)

    if (mode.applyFormat) {
      applyFormat(steps, pathConfig, config, logger)
    }

    if (mode.check) {
      checkFormat(steps, pathConfig, config, logger)
    }
  }

  override private[spotless] def getTarget: Seq[File] = {
    if (config.target == null || config.target.isEmpty) {
      return CppDefaults.FILE_FILTER.asScala.flatMap(filter =>
        GlobResolver.resolve(pathConfig.baseDir, filter)
      )
    }

    resolveTarget(config.target, pathConfig.baseDir)
  }

  override def getName: String = "spotlessCpp"

  override def getClassName: String = config.getClass.getSimpleName
}
