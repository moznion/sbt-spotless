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
import java.nio.file.Path

import com.diffplug.spotless.Provisioner
import com.diffplug.spotless.cpp.CppDefaults
import com.diffplug.spotless.extra.cpp.EclipseCdtFormatterStep
import net.moznion.sbt.spotless.config.CppConfig
import sbt.util.Logger

import _root_.scala.collection.JavaConverters._

private[sbt] case class Cpp[T <: CppConfig](
    private val config: T,
    private val baseDir: Path,
    private val logger: Logger,
) extends FormatRunnable[T] {
  def run(
      provisioner: Provisioner,
      mode: RunningMode,
  ): Unit = {
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
      applyFormat(steps, baseDir, config, logger)
    }

    if (mode.check) {
      checkFormat(steps, baseDir, config, logger)
    }
  }

  override private[spotless] def getTarget: Seq[File] = {
    if (config.target == null || config.target.isEmpty) {
      return CppDefaults.FILE_FILTER.asScala.flatMap(filter =>
        better.files.File(baseDir).glob(filter).map(found => found.toJava),
      )
    }

    resolveTarget(config.target, baseDir)
  }
}
