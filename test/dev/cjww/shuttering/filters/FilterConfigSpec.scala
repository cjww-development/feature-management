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

package dev.cjww.shuttering.filters

import org.scalatestplus.play.PlaySpec
import play.api.test.FakeRequest

class FilterConfigSpec extends PlaySpec {

  val testConfig = new FilterConfig {}

  "requestPathMatches" should {
    "return true" when {
      "the request path is a shutter on/off with true (full url)" in {
        implicit val request = FakeRequest("PATCH", "http://test.com/private/service-shuttering/true")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is a shutter on/off with false (full url)" in {
        implicit val request = FakeRequest("PATCH", "http://test.com/private/service-shuttering/false")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is a shutter on/off with true (partial url with private)" in {
        implicit val request = FakeRequest("PATCH", "/private/service-shuttering/true")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is a shutter on/off with false (partial url with private)" in {
        implicit val request = FakeRequest("PATCH", "/private/service-shuttering/false")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is a shutter on/off with true (partial url)" in {
        implicit val request = FakeRequest("PATCH", "/service-shuttering/true")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is a shutter on/off with false (partial url)" in {
        implicit val request = FakeRequest("PATCH", "/service-shuttering/false")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is a get shutter state route (full url)" in {
        implicit val request = FakeRequest("GET", "https://test.com/private/service-shuttering/state")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is a get shutter state route (partial url with private)" in {
        implicit val request = FakeRequest("GET", "/private/service-shuttering/state")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is a get shutter state route (partial url)" in {
        implicit val request = FakeRequest("GET", "/service-shuttering/state")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is a get specific feature route (full url)" in {
        implicit val request = FakeRequest("GET", "https://test.com/private/feature/test-feature/state")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is a get specific feature route (partial url with private)" in {
        implicit val request = FakeRequest("GET", "/private/feature/test-feature/state")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is a get specific feature route (partial url)" in {
        implicit val request = FakeRequest("GET", "/feature/test-feature/state")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is the get all features route (full url)" in {
        implicit val request = FakeRequest("GET", "https://test.com/private/features")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is the get all features route (partial url with private)" in {
        implicit val request = FakeRequest("GET", "/private/features")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is the get all features route (partial url)" in {
        implicit val request = FakeRequest("GET", "/features")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is the update feature route with true (full url)" in {
        implicit val request = FakeRequest("GET", "https://test.com/private/feature/test-feature/state/true")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is the update feature route with true (partial url with private)" in {
        implicit val request = FakeRequest("GET", "/private/feature/test-feature/state/true")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is the update feature route with true (partial url)" in {
        implicit val request = FakeRequest("GET", "/feature/test-feature/state/true")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is the update feature route with false (full url)" in {
        implicit val request = FakeRequest("GET", "https://test.com/private/feature/test-feature/state/false")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is the update feature route with false (partial url with private)" in {
        implicit val request = FakeRequest("GET", "/private/feature/test-feature/state/false")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is the update feature route with false (partial url)" in {
        implicit val request = FakeRequest("GET", "/feature/test-feature/state/false")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }

      "the request path is an assets route" in {
        implicit val request = FakeRequest("GET", "/test-service/assets/stylesheets/styles.css")

        val matches = testConfig.requestPathMatches
        assert(matches)
      }
    }

    "return false" when {
      "the request path is a shutter on/off but doesn't contain a boolean (full url)" in {
        implicit val request = FakeRequest("GET", "http://test.com/private/service-shuttering/invalid-string")

        val matches = testConfig.requestPathMatches
        assert(!matches)
      }

      "the request path is a shutter on/off but doesn't contain a boolean (partial url with private)" in {
        implicit val request = FakeRequest("GET", "/private/service-shuttering/invalid-string")

        val matches = testConfig.requestPathMatches
        assert(!matches)
      }

      "the request path is a shutter on/off but doesn't contain a boolean (partial url)" in {
        implicit val request = FakeRequest("GET", "/service-shuttering/invalid-string")

        val matches = testConfig.requestPathMatches
        assert(!matches)
      }

      "the request path is the update feature route with invalid state (full url)" in {
        implicit val request = FakeRequest("GET", "https://test.com/private/feature/test-feature/state/invalid")

        val matches = testConfig.requestPathMatches
        assert(!matches)
      }

      "the request path is the update feature route with invalid state (partial url with private)" in {
        implicit val request = FakeRequest("GET", "/private/feature/test-feature/state/invalid")

        val matches = testConfig.requestPathMatches
        assert(!matches)
      }

      "the request path is the update feature route with invalid state (partial url)" in {
        implicit val request = FakeRequest("GET", "/feature/test-feature/state/invalid")

        val matches = testConfig.requestPathMatches
        assert(!matches)
      }

      "the request doesn't match any configured routes" in {
        implicit val request = FakeRequest("GET", "/dashboard")

        val matches = testConfig.requestPathMatches
        assert(!matches)
      }
    }
  }
}
