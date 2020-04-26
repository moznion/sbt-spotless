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
import java.nio.charset.Charset
import java.nio.file.{Files, Path, StandardOpenOption}

import com.diffplug.spotless.extra.integration.DiffMessageFormatter
import com.diffplug.spotless.{Formatter, LineEnding, PaddedCell, PaddedCellBulk, Provisioner}
import net.moznion.sbt.spotless.Target.{IsFile, IsString}
import net.moznion.sbt.spotless.config.{FormatterConfig, SpotlessPathConfig}
import net.moznion.sbt.spotless.exception.{ShouldTurnOnPaddedCellException, ViolatedFormatException}
import net.moznion.sbt.spotless.{FormatterSteps, RunningMode, Target}
import sbt.util.Logger

import _root_.scala.collection.JavaConverters._

trait RunnableTask[T <: FormatterConfig] {
  private val paddedCellDescriptionURL =
    "https://github.com/diffplug/spotless/blob/master/PADDEDCELL.md"

  private[spotless] def getTarget: Seq[File]

  private[sbt] def run(provisioner: Provisioner, mode: RunningMode): Unit

  private[spotless] def resolveTarget(target: Seq[Target], baseDir: File): Seq[File] = {
    target.flatMap {
      case IsFile(file) => Seq(file)
      case IsString(strPath) =>
        better.files.File(baseDir.toPath).glob(strPath).map(found => found.toJava)
    }
  }

  /**
    * @throws ShouldTurnOnPaddedCellException
    * @throws ViolatedFormatException
    */
  private[spotless] def checkFormat(
      steps: FormatterSteps,
      pathConfig: SpotlessPathConfig,
      config: T,
      logger: Logger,
  ): Unit = {
    val target = getTarget.filterNot(Option(config.targetExclude).toSet)

    val formatter = buildFormatter(target, steps, pathConfig.baseDir.toPath, config)
    try {
      val problemFiles = getTarget.filter(file => !formatter.isClean(file))

      if (config.paddedCell) {
        checkWithPaddedCell(formatter, problemFiles, pathConfig, logger)
        return
      }

      if (problemFiles.nonEmpty) {
        if (PaddedCellBulk.anyMisbehave(formatter, problemFiles.asJava)) {
          throw ShouldTurnOnPaddedCellException(
            getName,
            getClassName,
            paddedCellDescriptionURL,
            pathConfig,
          )
        }
        throw ViolatedFormatException(
          DiffMessageFormatter
            .builder()
            .runToFix("Run 'sbt spotlessApply' to fix these violations.")
            .formatter(formatter)
            .problemFiles(problemFiles.asJava)
            .isPaddedCell(config.paddedCell)
            .getMessage,
        )
      }
    } finally {
      formatter.close()
    }
  }

  private def checkWithPaddedCell(
      formatter: Formatter,
      problemFiles: Seq[File],
      pathConfig: SpotlessPathConfig,
      logger: Logger,
  ): Unit = {
    if (problemFiles.isEmpty) {
      logger.info(s"""|$getName is in `paddedCell` mode, but it doesn't need to be.
                      |If you remove that option, spotless will run ~2x faster.
                      |For details see $paddedCellDescriptionURL""".stripMargin)
    }

    val stillFailingFiles = PaddedCellBulk.check(
      pathConfig.paddedCellWorkingDir,
      pathConfig.paddedCellDiagnoseDir,
      formatter,
      problemFiles.asJava,
    )
    if (!stillFailingFiles.isEmpty) {
      throw ViolatedFormatException(
        DiffMessageFormatter
          .builder()
          .runToFix("Run 'sbt spotlessApply' to fix these violations.")
          .formatter(formatter)
          .problemFiles(problemFiles.asJava)
          .isPaddedCell(true)
          .getMessage,
      )
    }
  }

  /**
    * @throws ShouldTurnOnPaddedCellException
    */
  private[spotless] def applyFormat(
      steps: FormatterSteps,
      pathConfig: SpotlessPathConfig,
      config: T,
      logger: Logger,
  ): Seq[File] = {
    val target = getTarget.filterNot(Option(config.targetExclude).toSet)

    val formatter = buildFormatter(target, steps, pathConfig.baseDir.toPath, config)
    try {
      if (config.paddedCell) {
        return target.filter(file => {
          logger.debug(s"applying format to $file")
          PaddedCellBulk.applyAnyChanged(formatter, file)
        })
      }

      var changed = List[File]()
      var anyMisbehave = false

      for (file <- target) {
        logger.debug(s"applying format to $file")
        val formatted: String = formatter.applyToAndReturnResultIfDirty(file)
        if (formatted != null) {
          changed :+= file
        }

        if (!anyMisbehave && formatted != null) {
          val onceMore: String = formatter.compute(formatted, file)
          if (!onceMore.equals(formatted)) {
            val paddedCellResult: PaddedCell =
              PaddedCell.check(formatter, file, onceMore)
            if (paddedCellResult.`type`() == PaddedCell.Type.CONVERGE) {
              val result: String =
                formatter.computeLineEndings(paddedCellResult.canonical(), file)
              Files.write(
                file.toPath,
                result.getBytes(formatter.getEncoding),
                StandardOpenOption.TRUNCATE_EXISTING,
              )
            } else {
              anyMisbehave = true
            }
          }
        }
      }
      if (anyMisbehave) {
        throw ShouldTurnOnPaddedCellException(
          getName,
          getClassName,
          paddedCellDescriptionURL,
          pathConfig,
        )
      }

      changed.reverse
    } finally {
      formatter.close()
    }
  }

  private def buildFormatter(
      target: Seq[File],
      steps: FormatterSteps,
      baseDir: Path,
      config: T,
  ): Formatter = {
    Formatter
      .builder()
      .rootDir(baseDir)
      .steps(steps.getSteps.asJava)
      .lineEndingsPolicy(
        Option(config.lineEndings)
          .map(l => l.createPolicy())
          .getOrElse(LineEnding.UNIX.createPolicy(baseDir.toFile, () => target.asJava)),
      )
      .encoding(Option(config.encoding).getOrElse(Charset.defaultCharset()))
      .exceptionPolicy(config.exceptionPolicy)
      .build()
  }

  def getName: String
  def getClassName: String
}
