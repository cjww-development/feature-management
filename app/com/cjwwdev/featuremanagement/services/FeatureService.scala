/*
 * Copyright 2018 CJWW Development
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

package com.cjwwdev.featuremanagement.services

import com.cjwwdev.featuremanagement.models.{Feature, Features}
import javax.inject.Inject

class DefaultFeatureService @Inject()() extends FeatureService

trait FeatureService {

  private val BASE_KEY = "features"

  def getState(featureName: String): Feature = {
    Option(System.getProperty(s"$BASE_KEY.$featureName"))
      .fold(Feature(featureName, state = false))(state => Feature(featureName, state = state.toBoolean))
  }

  def getAllStates(featureValue: Features): List[Feature] = {
    featureValue.allFeatures map getState
  }

  def setState(featureName: String, state: Boolean): Boolean = {
    System.setProperty(s"$BASE_KEY.$featureName", state.toString)
    if(System.getProperty(s"$BASE_KEY.$featureName").toBoolean == state) {
      state
    } else {
      throw new IllegalStateException(s"Could not set correct state for feature $featureName")
    }
  }
}
