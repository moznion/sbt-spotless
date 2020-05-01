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

package net.moznion.sbt.spotless.exception

import net.moznion.sbt.spotless.config.SpotlessPathConfig

object ShouldTurnOnPaddedCellException {
  private def message(
      taskName: String,
      configClassName: String,
      paddedCellDescriptionURL: String,
      pathConfig: SpotlessPathConfig
  ): String =
    s"""|You have a misbehaving rule which can't make up its mind.
       |This means that spotlessCheck will fail even after spotlessApply has run.
       |
       |This is a bug in a formatting rule, not Spotless itself, but Spotless can
       |work around this bug and generate helpful bug reports for the broken rule
       |if you add 'paddedCell = true' to your build.sbt as such:
       |
       |  $taskName := $configClassName(
       |    ...
       |    paddedCell = true,
       |  )
       |
       |The next time you run spotlessCheck, it will put helpful bug reports into
       |"${pathConfig.paddedCellDiagnoseDir.toString}", and spotlessApply
       |and spotlessCheck will be self-consistent from here on out.
       |
       |For details see: $paddedCellDescriptionURL""".stripMargin
}

case class ShouldTurnOnPaddedCellException(
    private val taskName: String,
    private val configClassName: String,
    private val paddedCellDescriptionURL: String,
    private val pathConfig: SpotlessPathConfig
) extends FormatException(
      ShouldTurnOnPaddedCellException
        .message(taskName, configClassName, paddedCellDescriptionURL, pathConfig)
    ) {}
