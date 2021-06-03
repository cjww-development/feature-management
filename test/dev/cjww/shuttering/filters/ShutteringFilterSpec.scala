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

import java.net.URI

import akka.stream.Materializer
import akka.util.ByteString
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.{HttpVerbs, Writeable}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.typedmap.TypedMap
import play.api.mvc.Results.Ok
import play.api.mvc.request.{RemoteConnection, RequestTarget}
import play.api.mvc.{Headers, RequestHeader, Result}
import play.api.test.Helpers._
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import play.twirl.api.Html

import scala.concurrent.Future

class ShutteringFilterSpec extends PlaySpec with GuiceOneAppPerSuite with FutureAwaits with DefaultAwaitTimeout {

  val materializer = app.injector.instanceOf[Materializer]

  val testBackendShutteringfilter = new ShutteringFilter[JsValue] {
    override def shutterResponse(statusCode: Int)(implicit rh: RequestHeader): JsValue = Json.parse(s"""{ "status" : ${statusCode} }""")
    override implicit val writer: Writeable[JsValue] = Writeable.writeableOf_JsValue
    override val appName: String = "test-backend-service"
    override implicit def mat: Materializer = materializer
  }

  val testFrontendShutteringFilter = new ShutteringFilter[Html] {
    override def shutterResponse(statusCode: Int)(implicit rh: RequestHeader): Html = Html(s"<p>Status: ${statusCode}</p>")
    override implicit val writer: Writeable[Html] = Writeable(html => ByteString(html.body), contentType = Some("text/html"))
    override val appName: String = "test-frontend-service"
    override implicit def mat: Materializer = materializer
  }

  val okFunction: RequestHeader => Future[Result] = rh => Future.successful(Ok("request allowed through"))

  def requestHeader(verb: String, route: String): RequestHeader = new RequestHeader {
    override def connection: RemoteConnection = ???
    override def method: String = verb
    override def target: RequestTarget = new RequestTarget {
      override def uri: URI = ???
      override def uriString: String = route
      override def path: String = ""
      override def queryMap: Map[String, Seq[String]] = ???
    }
    override def version: String = ???
    override def headers: Headers = ???
    override def attrs: TypedMap = ???
  }

  "shutteringFilter" should {
    "allow the request to proceed" when {
      "calling a frontend service and the requested route and method are whitelisted" in {
        System.setProperty("shuttered", "false")
        val res = testFrontendShutteringFilter.apply(okFunction)(requestHeader(HttpVerbs.GET, "/assets/"))
        status(res) mustBe OK
        contentAsString(res) mustBe "request allowed through"
      }

      "calling a backend service and the requested route and method are whitelisted" in {
        System.setProperty("shuttered", "false")
        val res = testBackendShutteringfilter.apply(okFunction)(requestHeader(HttpVerbs.GET, "/features"))
        status(res) mustBe OK
        contentAsString(res) mustBe "request allowed through"
      }

      "calling a frontend service and the requested route and method aren't whitelisted but the service isn't shuttered" in {
        System.setProperty("shuttered", "false")
        val res = testFrontendShutteringFilter.apply(okFunction)(requestHeader(HttpVerbs.HEAD, "/non-whitelisted-route"))
        status(res) mustBe OK
        contentAsString(res) mustBe "request allowed through"
      }

      "calling a backend service and the requested route and method aren't whitelisted but the service isn't shuttered" in {
        System.setProperty("shuttered", "false")
        val res = testBackendShutteringfilter.apply(okFunction)(requestHeader(HttpVerbs.DELETE, "/non-whitelisted-route"))
        status(res) mustBe OK
        contentAsString(res) mustBe "request allowed through"
      }
    }

    "block the request if the service is shuttered" when {
      "calling a frontend service" in {
        System.setProperty("shuttered", "true")
        val res = testFrontendShutteringFilter.apply(okFunction)(requestHeader(HttpVerbs.GET, "/some-route"))
        status(res) mustBe SERVICE_UNAVAILABLE
        contentAsString(res) mustBe "<p>Status: 503</p>"
      }

      "calling a backend service" in {
        System.setProperty("shuttered", "true")
        val res = testBackendShutteringfilter.apply(okFunction)(requestHeader(HttpVerbs.GET, "/some-route"))
        status(res) mustBe SERVICE_UNAVAILABLE
        contentAsJson(res) mustBe Json.parse("""{ "status" : 503 }""")
      }
    }
  }
}
