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

import com.diffplug.spotless.FormatterStep
import com.diffplug.spotless.generic.LicenseHeaderStep

/**
  * License header configuration with file for java.
  *
  * @param file License header file to prefix that before the package statement.
  * @param encoding Character encoding of the license header file.
  */
case class JavaLicenseFileHeaderConfig(file: File, encoding: Charset)
    extends LicenseHeaderConfig(JavaConfig.licenseHeaderDelimiter) {
  override def createStep: FormatterStep =
    LicenseHeaderStep.createFromFile(file, encoding, delimiter, yearSeparator)
}
