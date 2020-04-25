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
import java.nio.file.Path

import com.diffplug.spotless.Provisioner
import net.moznion.sbt.spotless._
import net.moznion.sbt.spotless.config._
import net.moznion.sbt.spotless.task._
import sbt.Keys._
import sbt.{Def, _}

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
      spotlessCheck := supplySpotlessTaskInitiator(RunningMode(check = true)).value,
      spotlessApply := supplySpotlessTaskInitiator(RunningMode(check = true, applyFormat = true)).value,
    )

  override def globalSettings: Seq[Def.Setting[_]] = Seq(
    spotless := SpotlessConfig(),
    spotlessJava := JavaConfig(enabled = false),
    spotlessScala := ScalaConfig(enabled = false),
    spotlessCpp := CppConfig(enabled = false),
    spotlessGroovy := GroovyConfig(enabled = false),
    spotlessKotlin := KotlinConfig(enabled = false),
    spotlessSql := SqlConfig(enabled = false),
  )

  private val supplySpotlessTaskInitiator: RunningMode => Def.Initialize[Task[Unit]] = {
    mode: RunningMode =>
      Def.task {
        val defaultBaseDir: File = thisProject.value.base
        val config: SpotlessConfig = spotless.value
        val pathConfig: SpotlessPathConfig = config.toPathConfig(defaultBaseDir, target.value)
        val baseDir: Path = pathConfig.baseDir.toPath

        val logger: Logger = streams.value.log
        val staticDeps: Seq[File] =
          (dependencyClasspathAsJars in Compile).value.map(dep => dep.data)
        val provisioner: Provisioner =
          SbtProvisioner.supplyProvisioner(config, pathConfig, staticDeps, logger)

        lazy val classPathFiles: Seq[File] =
          (sources in Compile).value.toList ++ (sources in Test).value.toList

        val javaConfig: JavaConfig = spotlessJava.value
        if (javaConfig.enabled) {
          val javaFiles =
            classPathFiles.filter(p => javaConfig.getExtensions.exists(ext => p.ext.equals(ext)))
          Java(javaFiles, javaConfig, baseDir, logger).run(provisioner, mode)
        }

        val scalaConfig: ScalaConfig = spotlessScala.value
        if (scalaConfig.enabled) {
          val scalaFiles =
            classPathFiles.filter(p => scalaConfig.getExtensions.exists(ext => p.ext.equals(ext)))
          Scala(scalaFiles, scalaConfig, baseDir, logger).run(provisioner, mode)
        }

        val cppConfig: CppConfig = spotlessCpp.value
        if (cppConfig.enabled) {
          Cpp(cppConfig, baseDir, logger).run(provisioner, mode)
        }

        val groovyConfig: GroovyConfig = spotlessGroovy.value
        if (groovyConfig.enabled) {
          Groovy(groovyConfig, baseDir, logger).run(provisioner, mode)
        }

        val kotlinConfig: KotlinConfig = spotlessKotlin.value
        if (kotlinConfig.enabled) {
          Kotlin(kotlinConfig, baseDir, logger).run(provisioner, mode)
        }

        val sqlConfig: SqlConfig = spotlessSql.value
        if (sqlConfig.enabled) {
          Sql(sqlConfig, baseDir, logger).run(provisioner, mode)
        }
      }
  }
}
