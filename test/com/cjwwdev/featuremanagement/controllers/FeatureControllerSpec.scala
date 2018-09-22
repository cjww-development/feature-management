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

package com.cjwwdev.featuremanagement.controllers

import java.util.UUID

import com.cjwwdev.config.{ConfigurationLoader, DefaultConfigurationLoader}
import com.cjwwdev.featuremanagement.models.{Feature, Features}
import com.cjwwdev.featuremanagement.services.FeatureService
import com.cjwwdev.http.headers.HeaderPackage
import com.cjwwdev.testing.unit.UnitTestSpec
import com.cjwwdev.implicits.ImplicitDataSecurity._
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers.stubControllerComponents

class FeatureControllerSpec extends UnitTestSpec with GuiceOneAppPerSuite {

  val mockFeatureService = mock[FeatureService]

  val configuration = app.injector.instanceOf[DefaultConfigurationLoader]

  object TestFeatures extends Features {
    val testFeature1 = "testFeature1"
    val testFeature2 = "testFeature2"

    override val allFeatures: List[String] = List(
      testFeature1,
      testFeature2
    )
  }

  val testController = new FeatureController {
    override protected val config: ConfigurationLoader                = configuration
    override val features: Features                                   = TestFeatures
    override val featureService: FeatureService                       = mockFeatureService
    override protected def controllerComponents: ControllerComponents = stubControllerComponents()
  }

  lazy val request = FakeRequest()
    .withHeaders(
      "cjww-headers" -> HeaderPackage("testAppId", Some(s"session-${UUID.randomUUID()}")).encrypt
    )

  "getState" should {
    "return an Ok" in {
      when(mockFeatureService.getState(ArgumentMatchers.any()))
        .thenReturn(Feature("testFeatureName", state = true))

      assertFutureResult(testController.getState("testFeatureName")(request)) { res =>
        status(res) mustBe OK
        contentAsJson(res) mustBe Json.parse(
          """
            |{
            |   "feature" : "testFeatureName",
            |   "state" : true
            |}
          """.stripMargin
        )
      }
    }

    "return a NotFound" in {
      assertFutureResult(testController.getState("testFeatureName")(FakeRequest())) {
        status(_) mustBe NOT_FOUND
      }
    }

    "return a Forbidden" in {
      val request = FakeRequest().withHeaders(
        "cjww-headers" -> HeaderPackage("invalid", Some(s"session-${UUID.randomUUID()}")).encrypt
      )

      assertFutureResult(testController.getState("testFeatureName")(request)) {
        status(_) mustBe FORBIDDEN
      }
    }
  }

  "getAllStates" should {
    "return an Ok" in {
      when(mockFeatureService.getAllStates(ArgumentMatchers.any()))
        .thenReturn(List(
          Feature("testFeature1", state = true),
          Feature("testFeature2", state = false)
        ))

      assertFutureResult(testController.getAllStates()(request)) { res =>
        status(res) mustBe OK
        contentAsJson(res) mustBe Json.parse(
          """
            |[
            |   {
            |       "feature" : "testFeature1",
            |       "state" : true
            |   },
            |   {
            |       "feature" : "testFeature2",
            |       "state" : false
            |   }
            |]
          """.stripMargin
        )
      }
    }

    "return a NotFound" in {
      assertFutureResult(testController.getAllStates()(FakeRequest())) {
        status(_) mustBe NOT_FOUND
      }
    }

    "return a Forbidden" in {
      val request = FakeRequest()
        .withHeaders(
          "cjww-headers" -> HeaderPackage("invalid", Some(s"session-${UUID.randomUUID()}")).encrypt
        )

      assertFutureResult(testController.getState("testFeatureName")(request)) {
        status(_) mustBe FORBIDDEN
      }
    }
  }

  "setState" should {
    "return an Ok" in {
      when(mockFeatureService.setState(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(true)

      assertFutureResult(testController.setState("testFeature", "true")(request)) { res =>
        status(res)        mustBe OK
        contentAsJson(res) mustBe Json.parse(
          """
            |{
            |   "testFeature" : "true"
            |}
          """.stripMargin
        )
      }
    }

    "return a Bad request" in {
      assertFutureResult(testController.setState("testFeature", "invalid")(request)) {
        status(_) mustBe BAD_REQUEST
      }
    }

    "return an Internal server error" in {
      when(mockFeatureService.setState(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(false)

      assertFutureResult(testController.setState("testFeature", "true")(request)) {
        status(_) mustBe INTERNAL_SERVER_ERROR
      }
    }

    "return a NotFound" in {
      assertFutureResult(testController.setState("testFeature", "true")(FakeRequest())) {
        status(_) mustBe NOT_FOUND
      }
    }

    "return a Forbidden" in {
      val request = FakeRequest()
        .withHeaders(
          "cjww-headers" -> HeaderPackage("invalid", Some(s"session-${UUID.randomUUID()}")).encrypt
        )

      assertFutureResult(testController.setState("testFeatureName", "false")(request)) {
        status(_) mustBe FORBIDDEN
      }
    }
  }
}
