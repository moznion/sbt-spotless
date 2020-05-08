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

import com.diffplug.spotless.Provisioner
import net.moznion.sbt.spotless._
import net.moznion.sbt.spotless.config._
import net.moznion.sbt.spotless.exception.FormatException
import net.moznion.sbt.spotless.task._
import sbt.Keys._
import sbt._

/**
  * An entry-point of sbt-spotless plugin.
  */
object SbtSpotless extends AutoPlugin {

  object autoImport {
    lazy val spotlessApply = taskKey[Unit]("apply the file format by Spotless")
    lazy val spotlessCheck = taskKey[Unit]("check the file format by Spotless")

    lazy val spotless = SettingKey[SpotlessConfig]("spotless", "spotless plugin configuration")
    lazy val spotlessJava =
      SettingKey[JavaConfig]("spotlessJava", "spotless configuration for Java")
    lazy val spotlessScala =
      SettingKey[ScalaConfig]("spotlessScala", "spotless configuration for Scala")
    lazy val spotlessCpp =
      SettingKey[CppConfig]("spotlessCpp", "spotless configuration for cpp")
    lazy val spotlessGroovy =
      SettingKey[GroovyConfig]("spotlessGroovy", "spotless configuration for Groovy")
    lazy val spotlessKotlin =
      SettingKey[KotlinConfig]("spotlessKotlin", "spotless configuration for Kotlin")
    lazy val spotlessSql =
      SettingKey[SqlConfig]("spotlessSql", "spotless configuration for SQL")
  }

  import autoImport._

  override def trigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] =
    super.projectSettings ++ Seq(
      spotlessCheck := supplySpotlessTaskInitiator(RunningMode(check = true), "spotlessCheck").value,
      spotlessApply := supplySpotlessTaskInitiator(
        RunningMode(check = true, applyFormat = true),
        "spotlessApply"
      ).value
    )

  override def globalSettings: Seq[Def.Setting[_]] = Seq(
    spotless := SpotlessConfig(),
    spotlessJava := JavaConfig(enabled = false),
    spotlessScala := ScalaConfig(enabled = false),
    spotlessCpp := CppConfig(enabled = false),
    spotlessGroovy := GroovyConfig(enabled = false),
    spotlessKotlin := KotlinConfig(enabled = false),
    spotlessSql := SqlConfig(enabled = false)
  )

  private val supplySpotlessTaskInitiator: (RunningMode, String) => Def.Initialize[Task[Unit]] = {
    (mode: RunningMode, taskName: String) =>
      Def.task {
        val defaultBaseDir: File = thisProject.value.base
        val config: SpotlessConfig = spotless.value
        val pathConfig: SpotlessPathConfig = config.toPathConfig(defaultBaseDir, target.value)

        val sbtLogger: sbt.Logger = streams.value.log
        val logger: net.moznion.sbt.spotless.Logger = new SbtSpotlessLogger(sbtLogger)
        val staticDeps: Seq[File] =
          (dependencyClasspath in Compile).value.map(dep => dep.data)
        val provisioner: Provisioner =
          SbtProvisioner.supplyProvisioner(
            config,
            pathConfig,
            staticDeps,
            new IvyDependencyResolver(sbtLogger),
            logger
          )

        lazy val classPathFiles: Seq[File] =
          (sources in Compile).value.toList ++ (sources in Test).value.toList

        val javaConfig: JavaConfig = spotlessJava.value

        var tasksToRun: Seq[RunnableTask[_]] = Seq()

        if (javaConfig.enabled) {
          val javaFiles =
            classPathFiles.filter(p => javaConfig.getExtensions.exists(ext => p.ext.equals(ext)))
          tasksToRun :+= Java(javaFiles, javaConfig, pathConfig, logger)
        }

        val scalaConfig: ScalaConfig = spotlessScala.value
        if (scalaConfig.enabled) {
          val scalaFiles =
            classPathFiles.filter(p => scalaConfig.getExtensions.exists(ext => p.ext.equals(ext)))
          tasksToRun :+= Scala(scalaFiles, scalaConfig, pathConfig, logger)
        }

        val cppConfig: CppConfig = spotlessCpp.value
        if (cppConfig.enabled) {
          tasksToRun :+= Cpp(cppConfig, pathConfig, logger)
        }

        val groovyConfig: GroovyConfig = spotlessGroovy.value
        if (groovyConfig.enabled) {
          tasksToRun :+= Groovy(groovyConfig, pathConfig, logger)
        }

        val kotlinConfig: KotlinConfig = spotlessKotlin.value
        if (kotlinConfig.enabled) {
          tasksToRun :+= Kotlin(kotlinConfig, pathConfig, logger)
        }

        val sqlConfig: SqlConfig = spotlessSql.value
        if (sqlConfig.enabled) {
          tasksToRun :+= Sql(sqlConfig, pathConfig, logger)
        }

        val succeeded: Boolean = tasksToRun
          .map(task =>
            try {
              task.run(provisioner, mode)
              true
            } catch {
              case e: FormatException =>
                logger.error(e.getMessage)
                false
            }
          )
          .reduce((acc, taskResult) => acc && taskResult)

        if (!succeeded) {
          if (!config.noFailOnViolated) {
            throw new MessageOnlyException(
              s"Failed to run $taskName, please refer to the above logs."
            )
          }
          logger.info(s"Some violations occur on $taskName, please refer to the above logs. `noFailOnViolated` is set true, so the task doesn't fail.")
        }
      }
  }
}
