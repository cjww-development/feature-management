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

import com.cjwwdev.config.{ConfigurationLoader, DefaultConfigurationLoader}
import com.cjwwdev.http.headers.HeaderPackage
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.testing.unit.UnitTestSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._

class ShutteringControllerSpec extends UnitTestSpec with GuiceOneAppPerSuite {

  val configuration = app.injector.instanceOf[DefaultConfigurationLoader]

  val testController = new ShutteringController {
    override protected val config: ConfigurationLoader = configuration
    override protected def controllerComponents: ControllerComponents = stubControllerComponents()
  }

  lazy val request = FakeRequest().withHeaders(
    "cjww-headers" -> HeaderPackage("testAppId", Some(s"session-${UUID.randomUUID()}")).encrypt
  )

  "shutter" should {
    "return an Ok and have set shuttered to true" in {
      assertFutureResult(testController.shutter()(request)) { res =>
        status(res)                     mustBe NO_CONTENT
        System.getProperty("shuttered") mustBe "true"
      }
    }

    "return a Forbidden" in {
      val request = FakeRequest().withHeaders(
        "cjww-headers" -> HeaderPackage("testAppId1", Some(s"session-${UUID.randomUUID()}")).encrypt
      )

      assertFutureResult(testController.shutter()(request)) {
        status(_) mustBe FORBIDDEN
      }
    }

    "return a NOT FOUND" in {
      assertFutureResult(testController.shutter()(FakeRequest())) {
        status(_) mustBe NOT_FOUND
      }
    }
  }

  "unshutter" should {
    "return an Ok and have set shuttered to false" in {
      assertFutureResult(testController.unshutter()(request)) { res =>
        status(res)                     mustBe NO_CONTENT
        System.getProperty("shuttered") mustBe "false"
      }
    }

    "return a Forbidden" in {
      val request = FakeRequest().withHeaders(
        "cjww-headers" -> HeaderPackage("testAppId1", Some(s"session-${UUID.randomUUID()}")).encrypt
      )

      assertFutureResult(testController.unshutter()(request)) {
        status(_) mustBe FORBIDDEN
      }
    }

    "return a NOT FOUND" in {
      assertFutureResult(testController.unshutter()(FakeRequest())) {
        status(_) mustBe NOT_FOUND
      }
    }
  }

  "getShutterState" should {
    "return an ok with the current state in the body" in {
      assertFutureResult(testController.getShutterState()(request)) { res =>
        status(res)                             mustBe OK
        contentAsJson(res).\("body").as[String] mustBe "false"
      }
    }

    "return a Forbidden" in {
      val request = FakeRequest().withHeaders(
        "cjww-headers" -> HeaderPackage("testAppId1", Some(s"session-${UUID.randomUUID()}")).encrypt
      )

      assertFutureResult(testController.getShutterState()(request)) {
        status(_) mustBe FORBIDDEN
      }
    }

    "return a NOT FOUND" in {
      assertFutureResult(testController.getShutterState()(FakeRequest())) {
        status(_) mustBe NOT_FOUND
      }
    }
  }
}
