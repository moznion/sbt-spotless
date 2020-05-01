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
  private[config] val licenseHeaderDelimiter = "package "
}

/**
  * A formatter configuration for java files.
  *
  * @param paddedCell A specifier whether to enable paddedCell mode or not. For more detail, please refer to the following doc: [[https://github.com/diffplug/spotless/blob/master/PADDEDCELL.md PADDEDCELL.md]]
  * @param lineEndings Represents the line endings which should be written by the tool.
  * @param encoding A character encoding that is used for code formatter.
  * @param exceptionPolicy A policy for handling exceptions in the format.
  * @param target A seq of target files to check/format.
  * @param targetExclude A seq of files to exclude from the target fo checking/formatting.
  * @param enabled A specifier whether to enable this formatter or not.
  * @param importOrder A seq of import order for java files.
  * @param importOrderFile A file that contains import order for java files.
  * @param removeUnusedImports A specifier of whether to remove unused imports or not.
  * @param googleJavaFormat A google-java-format configuration to format java files .
  * @param eclipseJava A java eclipse configuration to format java files.
  * @param licenseHeader License header string to prefix a that before the package statement.
  * @param licenseHeaderFile License header file to prefix a that before the package statement.
  */
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
    licenseHeaderFile: JavaLicenseFileHeaderConfig = null
) extends FormatterConfig(
      paddedCell,
      lineEndings,
      encoding,
      exceptionPolicy,
      target,
      targetExclude,
      enabled
    )
    with ExtensionsSupplier {
  private[sbt] override def getExtensions: Seq[String] = {
    List[String]("java")
  }
}
