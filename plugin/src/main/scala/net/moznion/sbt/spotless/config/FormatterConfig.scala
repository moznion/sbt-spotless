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

import java.nio.charset.Charset

import com.diffplug.spotless.{FormatExceptionPolicy, LineEnding}
import net.moznion.sbt.spotless.Target

/**
  * FormatterConfig is a generic configuration for each formatter.
  *
  * @param paddedCell A specifier whether to enable paddedCell mode or not. For more detail, please refer to the following doc: [[https://github.com/diffplug/spotless/blob/master/PADDEDCELL.md PADDEDCELL.md]]
  * @param lineEndings Represents the line endings which should be written by the tool.
  * @param encoding A character encoding that is used for code formatter.
  * @param exceptionPolicy A policy for handling exceptions in the format.
  * @param target A seq of target files to check/format.
  * @param targetExclude A seq of files to exclude from the target fo checking/formatting.
  * @param enabled A specifier whether to enable this formatter or not.
  */
abstract class FormatterConfig(
    val paddedCell: Boolean,
    val lineEndings: LineEnding,
    val encoding: Charset,
    val exceptionPolicy: FormatExceptionPolicy,
    val target: Seq[Target],
    val targetExclude: Seq[Target],
    val enabled: Boolean
) {}
