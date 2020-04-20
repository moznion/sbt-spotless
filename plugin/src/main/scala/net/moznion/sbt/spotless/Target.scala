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

package net.moznion.sbt.spotless

import java.io.File

import scala.language.implicitConversions

sealed trait Target

/**
  * A specifier for location of a file.
  *
  * This type accepts `String` or `File` instance.
  * If the value is instance of `File`, it locates the file according to the instance contents.
  * Else, when the value is instance of `String`, it locates the file by `${baseDir}/${stringTargetPath}`.
  */
object Target {
  case class IsString(str: String) extends Target
  case class IsFile(file: File) extends Target

  implicit def isString(str: String): Target = IsString(str)
  implicit def isFile(file: File): Target = IsFile(file)
}
