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

/**
  * A configuration for [[https://github.com/google/google-java-format google/google-java-format]].
  *
  * @param version The version of google-java-format.
  * @param style The style specifier for the formatter. Ref: [[https://github.com/google/google-java-format/blob/master/core/src/main/java/com/google/googlejavaformat/java/JavaFormatterOptions.java]]
  */
case class GoogleJavaFormatConfig(version: String = null, style: String = null) {}
