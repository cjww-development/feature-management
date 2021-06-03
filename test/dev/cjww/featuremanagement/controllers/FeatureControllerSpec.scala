/*
 * Copyright 2021 CJWW Development
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

package dev.cjww.featuremanagement.controllers

import java.util.UUID
import dev.cjww.featuremanagement.models.{Feature, Features}
import dev.cjww.featuremanagement.services.FeatureService
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents, RequestHeader, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._

class FeatureControllerSpec extends PlaySpec with MockitoSugar {

  val mockFeatureService: FeatureService = mock[FeatureService]

  val uuid: String = UUID.randomUUID().toString

  object TestFeatures extends Features {
    val testFeature1 = "testFeature1"
    val testFeature2 = "testFeature2"

    override val allFeatures: List[String] = List(
      testFeature1,
      testFeature2
    )
  }

  val testController = new FeatureController {
    override val features: Features                                   = TestFeatures
    override val featureService: FeatureService                       = mockFeatureService
    override protected def controllerComponents: ControllerComponents = stubControllerComponents()

    val adminAppId = uuid

    override def validateAdminCall(f: RequestHeader => Result): Action[AnyContent] = Action { implicit req =>
      req.headers.get("X-App-Id").fold(NotFound("")) {
        case `adminAppId` => f(req)
        case _ => Forbidden("")
      }
    }

    override def jsonResponse(status: Int, body: JsValue)(f: JsValue => Result)(implicit rh: RequestHeader): Result = {
      val json = Json.parse(
        s"""{
           | "status" : $status,
           | "body" : $body
           |}""".stripMargin)

      f(json)
    }
  }

  lazy val request = FakeRequest()
    .withHeaders("X-App-Id" -> uuid)

  "getState" should {
    "return an Ok" in {
      when(mockFeatureService.getState(ArgumentMatchers.any()))
        .thenReturn(Some(Feature("testFeatureName", state = true)))

      val res = testController.getState("testFeatureName")(request)
      status(res) mustBe OK
      contentAsJson(res) mustBe Json.parse(
        """
          |{
          | "status" : 200,
          | "body" : {
          |   "feature" : "testFeatureName",
          |   "state" : true
          | }
          |}
          |""".stripMargin
      )
    }

    "return a NoContent" in {
      when(mockFeatureService.getState(ArgumentMatchers.any()))
        .thenReturn(None)

      val res = testController.getState("testFeatureName")(request)
      status(res) mustBe NO_CONTENT
    }

    "return a NotFound" in {
      val res = testController.getState("testFeatureName")(FakeRequest())
      status(res) mustBe NOT_FOUND
    }

    "return a Forbidden" in {
      val request = FakeRequest()
        .withHeaders("X-App-Id" -> "invalid-app-id")

      val res = testController.getState("testFeatureName")(request)
      status(res) mustBe FORBIDDEN
    }
  }

  "getAllStates" should {
    "return an Ok" in {
      when(mockFeatureService.getAllStates(ArgumentMatchers.any()))
        .thenReturn(List(
          Feature("testFeature1", state = true),
          Feature("testFeature2", state = false)
        ))

      val res = testController.getAllStates()(request)
      status(res) mustBe OK
      contentAsJson(res) mustBe Json.parse(
        """
          |{
          | "status" : 200,
          | "body" : [
          |   {
          |       "feature" : "testFeature1",
          |       "state" : true
          |   },
          |   {
          |       "feature" : "testFeature2",
          |       "state" : false
          |   }
          | ]
          |}
          """.stripMargin
      )
    }

    "return an NoContent" in {
      when(mockFeatureService.getAllStates(ArgumentMatchers.any()))
        .thenReturn(List())

      val res = testController.getAllStates()(request)
      status(res) mustBe NO_CONTENT
    }

    "return a NotFound" in {
      val res = testController.getAllStates()(FakeRequest())
      status(res) mustBe NOT_FOUND
    }

    "return a Forbidden" in {
      val request = FakeRequest()
        .withHeaders("X-App-Id" -> "invalid-test-id")

      val res = testController.getState("testFeatureName")(request)
      status(res) mustBe FORBIDDEN
    }
  }

  "setState" should {
    "return an Ok" in {
      when(mockFeatureService.setState(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(true)

      val res = testController.setState("testFeature", "true")(request)
      status(res) mustBe OK
      contentAsJson(res) mustBe Json.parse(
        """
          |{
          | "status" : 200,
          | "body" : {
          |   "testFeature" : true
          | }
          |}
          """.stripMargin
      )
    }

    "return a Bad request" in {
      val res = testController.setState("testFeature", "invalid")(request)
      status(res) mustBe BAD_REQUEST
      contentAsJson(res) mustBe Json.parse(
        """
          |{
          | "status" : 400,
          | "body" : "Provide a boolean state (true or false); found invalid"
          |}
          |""".stripMargin
      )
    }

    "return an Internal server error" in {
      when(mockFeatureService.setState(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(false)

      val res = testController.setState("testFeature", "true")(request)
      status(res) mustBe INTERNAL_SERVER_ERROR
      contentAsJson(res) mustBe Json.parse(
        """
          |{
          | "status" : 500,
          | "body" : "There was a problem setting the state for feature testFeature"
          |}
          |""".stripMargin
      )
    }

    "return a NotFound" in {
      val res = testController.setState("testFeature", "true")(FakeRequest())
      status(res) mustBe NOT_FOUND
    }

    "return a Forbidden" in {
      val request = FakeRequest()
        .withHeaders("X-App-Id" -> "invalid-app-id")

      val res = testController.setState("testFeature", "false")(request)
      status(res) mustBe FORBIDDEN
    }
  }
}
