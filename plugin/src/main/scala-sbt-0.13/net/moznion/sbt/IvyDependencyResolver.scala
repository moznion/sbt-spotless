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
import org.apache.ivy.core.module.descriptor.{DefaultDependencyDescriptor, DefaultModuleDescriptor}
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.core.resolve.ResolveOptions
import org.apache.ivy.core.retrieve.RetrieveOptions
import sbt._
import scala.collection.JavaConverters._

class IvyDependencyResolver(private val logger: Logger) extends DependencyResolver {
  override def retrieve(
      org: String,
      name: String,
      rev: String,
      withTransitives: Boolean,
      dynamicDependencyWorkingDir: File
  ): Either[RuntimeException, Seq[File]] = {
    val ivyBaseDir = new File(dynamicDependencyWorkingDir, "jars")
    val ivyConfig = new InlineIvyConfiguration(
      new IvyPaths(ivyBaseDir, None),
      Resolver.withDefaultResolvers(Nil),
      Nil,
      Nil,
      false,
      None,
      Nil,
      None,
      sbt.UpdateOptions(),
      logger
    )

    new IvySbt(ivyConfig).withIvy(logger)(ivy => {
      val resolveOptions = new ResolveOptions();
      resolveOptions.setTransitive(withTransitives)
      resolveOptions.setDownload(true)

      val moduleDescriptor = DefaultModuleDescriptor.newDefaultInstance(
        ModuleRevisionId.newInstance(org, s"${name}-envelope", rev)
      )

      val resource = ModuleRevisionId.newInstance(org, name, rev)
      val dependencyDescriptor =
        new DefaultDependencyDescriptor(moduleDescriptor, resource, false, false, withTransitives)

      // ref: http://lightguard-jp.blogspot.de/2009/04/ivy-configurations-when-pulling-from.html
      dependencyDescriptor.addDependencyConfiguration("default", "default")
      moduleDescriptor.addDependency(dependencyDescriptor)

      val resolveResult = ivy.resolve(moduleDescriptor, resolveOptions)
      if (resolveResult.hasError) {
        throw new RuntimeException(resolveResult.getAllProblemMessages.toString)
      }

      val retrieveOptions =
        new RetrieveOptions(new RetrieveOptions().setConfs(Array[String]("default")))
      retrieveOptions.setDestArtifactPattern(
        ivyBaseDir.getAbsolutePath + "/[artifact](-[classifier]).[ext]"
      )
      val retrieveReport = ivy.retrieve(moduleDescriptor.getModuleRevisionId, retrieveOptions);

      Right(
        retrieveReport.getRetrievedFiles.asScala
          .map(shouldBeFile => shouldBeFile.asInstanceOf[File])
          .toSeq
      )
    })
  }
}
