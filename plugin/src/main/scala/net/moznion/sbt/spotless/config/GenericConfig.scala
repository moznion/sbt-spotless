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

class GenericConfig(
    val paddedCell: Boolean,
    val lineEndings: LineEnding,
    val encoding: Charset,
    val exceptionPolicy: FormatExceptionPolicy,
    val target: Seq[Target],
    val targetExclude: Seq[Target],
    val enabled: Boolean,
) {}
