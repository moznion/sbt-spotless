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

package net.moznion.sbt.spotless.config

import java.io.File
import java.nio.charset.Charset

import com.diffplug.spotless.{FormatExceptionPolicy, LineEnding}
import net.moznion.sbt.spotless.Target

object JavaConfig {
  val licenseHeaderDelimiter = "package "
}

case class JavaConfig(
    override val paddedCell: Boolean = false,
    override val lineEndings: LineEnding = null,
    override val encoding: Charset = null,
    override val exceptionPolicy: FormatExceptionPolicy = null,
    override val target: Seq[Target] = null,
    override val targetExclude: Seq[Target] = null,
    override val enabled: Boolean = true,
    importOrder: Seq[String] = null,
    importOrderFile: File = null,
    removeUnusedImports: Boolean = false,
    googleJavaFormat: GoogleJavaFormatConfig = null,
    eclipseJava: EclipseJavaConfig = null,
    licenseHeader: JavaLicenseStringHeaderConfig = null,
    licenseHeaderFile: JavaLicenseFileHeaderConfig = null,
) extends GenericConfig(
      paddedCell,
      lineEndings,
      encoding,
      exceptionPolicy,
      target,
      targetExclude,
      enabled,
    )
    with ExtensionsSupplier {
  override def getExtensions: Seq[String] = {
    List[String]("java")
  }
}
