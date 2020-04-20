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

import com.diffplug.spotless.{FormatterStep, SerializableFileFilter}
import net.moznion.sbt.spotless.exception.{MultipleStepsException, ReplacingStepException}

private case class FormatterSteps(
    private var steps: List[FormatterStep] = List(),
) {
  def getSteps: List[FormatterStep] = steps.reverse

  def addStep(step: FormatterStep): FormatterSteps = {
    if (getExistingStep(step.getName).nonEmpty) {
      throw MultipleStepsException(
        s"multiple steps with name '${step.getName}' for spotless format", // TODO more meaningful message
      )
    }
    FormatterSteps(steps :+ step)
  }

  def filterByName(
      stepName: String,
      filter: SerializableFileFilter,
  ): FormatterSteps = {
    FormatterSteps(steps.map(step => {
      if (stepName.equals(step.getName)) {
        step.filterByFile(filter)
      } else {
        step
      }
    }))
  }

  def getExistingStep(stepName: String): Option[FormatterStep] =
    steps.find(step => step.getName.equals(stepName))

  def getExistingStepIdx(stepName: String): Option[Int] = {
    val idx = steps.indexWhere(step => step.getName.equals(stepName))
    if (idx < 0) {
      return Option.empty
    }
    Option(idx)
  }

  def replaceStep(step: FormatterStep): List[FormatterStep] = {
    val maybeIdx = getExistingStepIdx(step.getName)
    if (maybeIdx.isEmpty) {
      throw ReplacingStepException(
        s"cannot replace step '${step.getName}' for spotless format", // TODO more meaningful message
      )
    }
    steps.updated(maybeIdx.get, step)
  }
}
