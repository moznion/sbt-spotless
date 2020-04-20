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

/**
  * GrEclipse formatter configuration for groovy files.
  *
  * @param version The version of GrEclipse formatter. Please refer to the following version definition: [[https://github.com/diffplug/spotless/tree/master/lib-extra/src/main/resources/com/diffplug/spotless/extra/groovy_eclipse_formatter]]
  * @param configFiles A seq of files for groovy eclipse configuration.
  */
case class GrEclipseConfig(version: String = null, configFiles: Seq[File] = null) {}
