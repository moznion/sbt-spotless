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
import com.diffplug.spotless.sql.DBeaverSQLFormatterStep
import net.moznion.sbt.spotless.config.{SpotlessPathConfig, SqlConfig}
import net.moznion.sbt.spotless.{FormatterSteps, Logger, RunningMode}

import _root_.scala.collection.JavaConverters._

private[sbt] case class Sql[T <: SqlConfig](
    private val config: T,
    private val pathConfig: SpotlessPathConfig,
    private val logger: Logger,
) extends RunnableTask[T] {
  def run(provisioner: Provisioner, mode: RunningMode): Unit = {
    if (!config.enabled) {
      return
    }

    var steps = FormatterSteps()

    steps = Option(config.dbeaver)
      .map(dbeaver => {
        Option(dbeaver.configFiles)
          .map(files => steps.addStep(DBeaverSQLFormatterStep.create(files.asJava)))
          .getOrElse(steps)
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
      return better.files
        .File(pathConfig.baseDir.toPath)
        .glob("**/*.sql")
        .map(found => found.toJava)
        .toSeq
    }

    resolveTarget(config.target, pathConfig.baseDir)
  }

  override def getName: String = "spotlessSql"

  override def getClassName: String = config.getClass.getSimpleName
}
