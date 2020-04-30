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

import net.moznion.sbt.spotless.Logger

class SbtSpotlessLogger(l: sbt.Logger) extends Logger {
  override def debug(message: => String): Unit = l.debug(message)

  override def info(message: => String): Unit = l.info(message)

  override def warn(message: => String): Unit = l.warn(message)

  override def error(message: => String): Unit = l.error(message)
}
