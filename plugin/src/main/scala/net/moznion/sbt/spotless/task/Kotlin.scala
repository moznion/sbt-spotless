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
import com.diffplug.spotless.kotlin.KtLintStep
import net.moznion.sbt.spotless.config.{KotlinConfig, SpotlessPathConfig}
import net.moznion.sbt.spotless.{FormatterSteps, RunningMode}
import sbt.util.Logger

import _root_.scala.collection.JavaConverters._

private[sbt] case class Kotlin[T <: KotlinConfig](
    private val config: T,
    private val pathConfig: SpotlessPathConfig,
    private val logger: Logger,
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

    steps = Option(config.ktlint)
      .map(ktLintConfig => {
        val version = Option(ktLintConfig.version)
          .getOrElse(KtLintStep.defaultVersion())
        val userData = Option(ktLintConfig.userData).getOrElse(Map()).asJava

        steps.addStep(KtLintStep.create(version, provisioner, userData))
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
      return List("kt", "ktm", "kts").flatMap(ext =>
        better.files.File(pathConfig.baseDir.toPath).glob("**/*." + ext).map(found => found.toJava),
      )
    }

    resolveTarget(config.target, pathConfig.baseDir)
  }

  override def getName: String = "spotlessKotlin"
}
