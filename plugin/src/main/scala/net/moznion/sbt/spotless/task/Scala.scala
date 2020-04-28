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
import com.diffplug.spotless.scala.ScalaFmtStep
import net.moznion.sbt.spotless.config.{ScalaConfig, SpotlessPathConfig}
import net.moznion.sbt.spotless.{FormatterSteps, Logger, RunningMode}

private[sbt] case class Scala[T <: ScalaConfig](
    private val scalaFiles: Seq[File],
    private val config: T,
    private val pathConfig: SpotlessPathConfig,
    private val logger: Logger
) extends RunnableTask[T] {
  def run(provisioner: Provisioner, mode: RunningMode): Unit = {
    if (!config.enabled) {
      return
    }

    var steps = FormatterSteps()

    steps = Option(config.scalafmt)
      .map(scalafmt => {
        val version: String =
          Option(scalafmt.version).getOrElse(ScalaFmtStep.defaultVersion())
        steps.addStep(
          ScalaFmtStep.create(version, provisioner, scalafmt.configFile)
        )
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
      return scalaFiles
    }

    resolveTarget(config.target, pathConfig.baseDir)
  }

  override def getName: String = "spotlessScala"

  override def getClassName: String = config.getClass.getSimpleName
}
