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

import java.io.File

import net.moznion.sbt.spotless.DependencyResolver
import sbt.librarymanagement.ModuleID
import sbt.librarymanagement.ivy.{InlineIvyConfiguration, IvyDependencyResolution}
import sbt.util.Logger

class IvyDependencyResolver(private val logger: Logger) extends DependencyResolver {
  override def retrieve(
      org: String,
      name: String,
      rev: String,
      dynamicDependencyWorkingDir: File,
  ): Either[RuntimeException, Seq[File]] = {
    IvyDependencyResolution(
      InlineIvyConfiguration().withLog(logger),
    ).retrieve(ModuleID(org, name, rev), None, dynamicDependencyWorkingDir, logger) match {
      case Left(warn)            => Left(warn.resolveException)
      case Right(retrievedFiles) => Right(retrievedFiles)
    }
  }
}
