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

package com.cjwwdev.shuttering.controllers

import java.util.UUID

import com.cjwwdev.http.headers.HeaderPackage
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.testing.unit.UnitTestSpec
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._

class ShutteringControllerSpec extends UnitTestSpec {

  val testController = new ShutteringController {
    override protected def controllerComponents: ControllerComponents = stubControllerComponents()
  }

  lazy val request = FakeRequest().withHeaders(
    "cjww-headers" -> HeaderPackage("testAppId", s"session-${UUID.randomUUID()}").encryptType
  )

  "shutter" should {
    "return an Ok and have set shuttered to true" in {
      val result = testController.shutter()(request)

      status(result) mustBe OK

      System.getProperty("shuttered") mustBe "true"
    }
  }

  "unshutter" should {
    "return an Ok and have set shuttered to false" in {
      val result = testController.unshutter()(request)

      status(result) mustBe OK

      System.getProperty("shuttered") mustBe "false"
    }
  }
}
