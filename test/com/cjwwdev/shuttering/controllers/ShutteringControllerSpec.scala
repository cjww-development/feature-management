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

package com.cjwwdev.shuttering.controllers

import java.util.UUID

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._

class ShutteringControllerSpec extends PlaySpec {

  val uuid: String = UUID.randomUUID().toString

  val testController: ShutteringController = new ShutteringController {
    private val adminAppId = uuid
    override protected def controllerComponents: ControllerComponents = stubControllerComponents()
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

  lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
    .withHeaders("X-App-Id" -> uuid)

  "shutter" should {
    "return an Ok and have set shuttered to true" in {
      val res = testController.shutter()(request)
      status(res) mustBe NO_CONTENT
      assert(System.getProperty("shuttered").toBoolean)
    }

    "return a Forbidden" in {
      val request = FakeRequest()
        .withHeaders("X-App-Id" -> "invalid-id")

      val res = testController.shutter()(request)
      status(res) mustBe FORBIDDEN
    }

    "return a NOT FOUND" in {
      val res = testController.shutter()(FakeRequest())
      status(res) mustBe NOT_FOUND
    }
  }

  "unShutter" should {
    "return an Ok and have set shuttered to false" in {
      val res = testController.unShutter()(request)
      status(res) mustBe NO_CONTENT
      assert(!System.getProperty("shuttered").toBoolean)
    }

    "return a Forbidden" in {
      val request = FakeRequest()
        .withHeaders("X-App-Id" -> "invalid-id")

      val res = testController.unShutter()(request)
      status(res) mustBe FORBIDDEN
    }

    "return a NOT FOUND" in {
      val res = testController.unShutter()(FakeRequest())
      status(res) mustBe NOT_FOUND
    }
  }

  "getShutterState" should {
    "return an ok with the current state in the body" in {
      val res = testController.getShutterState()(request)
      status(res) mustBe OK
      contentAsJson(res).\("body").as[String] mustBe "false"
    }

    "return a Forbidden" in {
      val request = FakeRequest()
        .withHeaders("X-App-Id" -> "invalid-id")

      val res = testController.getShutterState()(request)
      status(res) mustBe FORBIDDEN
    }

    "return a NOT FOUND" in {
      val res = testController.getShutterState()(FakeRequest())
      status(res) mustBe NOT_FOUND
    }
  }
}
