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
import com.diffplug.spotless.extra.java.EclipseJdtFormatterStep
import com.diffplug.spotless.generic.LicenseHeaderStep
import com.diffplug.spotless.java.{GoogleJavaFormatStep, ImportOrderStep, RemoveUnusedImportsStep}
import net.moznion.sbt.spotless.config.{JavaConfig, SpotlessPathConfig}
import net.moznion.sbt.spotless.{FormatterSteps, Logger, RunningMode}

import _root_.scala.collection.JavaConverters._

private[sbt] case class Java[T <: JavaConfig](
    private val javaFiles: Seq[File],
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

    steps = Option(config.importOrder)
      .map(importOrder => steps.addStep(ImportOrderStep.forJava().createFrom(importOrder: _*)))
      .getOrElse(steps)

    steps = Option(config.importOrderFile)
      .map(importOrderFile => steps.addStep(ImportOrderStep.forJava().createFrom(importOrderFile)))
      .getOrElse(steps)

    if (config.removeUnusedImports) {
      steps = steps.addStep(RemoveUnusedImportsStep.create(provisioner))
    }

    steps = Option(config.googleJavaFormat)
      .map(googleJavaFormat => {
        val version = Option(googleJavaFormat.version)
          .getOrElse(GoogleJavaFormatStep.defaultVersion())
        val style = Option(googleJavaFormat.style)
          .getOrElse(GoogleJavaFormatStep.defaultStyle())
        steps.addStep(GoogleJavaFormatStep.create(version, style, provisioner))
      })
      .getOrElse(steps)

    steps = Option(config.eclipseJava)
      .map(eclipseJavaFormat => {
        val version = Option(eclipseJavaFormat.version)
          .getOrElse(EclipseJdtFormatterStep.defaultVersion())
        val configFiles = Option(eclipseJavaFormat.configFiles).getOrElse(List())

        val builder = EclipseJdtFormatterStep.createBuilder(provisioner)
        builder.setVersion(version)
        builder.setPreferences(configFiles.asJava)

        steps.addStep(builder.build())
      })
      .getOrElse(steps)

    steps = steps.filterByName(
      LicenseHeaderStep.name(),
      LicenseHeaderStep.unsupportedJvmFilesFilter()
    )

    if (mode.applyFormat) {
      applyFormat(steps, pathConfig, config, logger)
    }

    if (mode.check) {
      checkFormat(steps, pathConfig, config, logger)
    }
  }

  override private[spotless] def getTarget: Seq[File] = {
    if (config.target == null || config.target.isEmpty) {
      return javaFiles
    }

    resolveTarget(config.target, pathConfig.baseDir)
  }

  override def getName: String = "spotlessJava"

  override def getClassName: String = config.getClass.getSimpleName
}
