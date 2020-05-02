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

package net.moznion.sbt.spotless.util

import java.io.File
import java.nio.file.{FileSystems, Files}

import _root_.scala.collection.JavaConverters._

object GlobResolver {
  def resolve(baseDir: File, glob: String): Seq[File] = {
    val globMatcher = FileSystems.getDefault.getPathMatcher("glob:" + glob)
    val baseDirPath = baseDir.toPath
    Files
      .walk(baseDirPath)
      .iterator()
      .asScala
      .filter(path => globMatcher.matches(baseDirPath.relativize(path)))
      .map[File](targetFilePath => targetFilePath.toFile)
      .toList
  }
}
