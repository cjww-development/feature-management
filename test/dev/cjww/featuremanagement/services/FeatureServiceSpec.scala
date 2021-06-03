/*
 * Copyright 2020 CJWW Development
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

package dev.cjww.featuremanagement.services

import dev.cjww.featuremanagement.models.{Feature, Features}
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec

class FeatureServiceSpec extends PlaySpec with BeforeAndAfterAll {

  val testService = new DefaultFeatureService

  object TestFeatures extends Features {
    val testFeature1 = "testFeature1"
    val testFeature2 = "testFeature2"

    override val allFeatures: List[String] = List(
      testFeature1,
      testFeature2
    )
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    System.setProperty("features.testFeature1", "true")
    System.setProperty("features.testFeature2", "false")
  }

  "getState" should {
    "return a feature with a true state" in {
      val result = testService.getState("testFeature1")
      result mustBe Some(Feature(feature = "testFeature1", state = true))
    }

    "return a feature with a false state" in {
      val result = testService.getState("testFeature2")
      result mustBe Some(Feature(feature = "testFeature2", state = false))
    }

    "return None when the feature doesn't exist" in {
      val result = testService.getState("testFeature3")
      result mustBe None
    }
  }

  "getAllStates" should {
    "return a list of features" in {
      val result = testService.getAllStates(TestFeatures)
      result mustBe List(
        Feature(feature = "testFeature1", state = true),
        Feature(feature = "testFeature2", state = false)
      )
    }
  }

  "setState" should {
    "return a boolean" in {
      val result = testService.setState("testFeature4", state = true)
      val actual = System.getProperty("features.testFeature4").toBoolean
      assert(result == actual)
    }
  }
}
