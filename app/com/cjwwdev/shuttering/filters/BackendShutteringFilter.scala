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

package com.cjwwdev.shuttering.filters

import akka.stream.Materializer
import com.cjwwdev.logging.Logging
import com.cjwwdev.request.RequestBuilder
import com.cjwwdev.responses.ApiResponse
import javax.inject.Inject
import play.api.http.HttpVerbs
import play.api.mvc.{Filter, Request, RequestHeader, Result}
import play.api.http.Status.SERVICE_UNAVAILABLE
import play.api.libs.json.JsString
import play.api.mvc.Results.ServiceUnavailable

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class BackendShutteringFilter @Inject()(implicit val mat: Materializer) extends Filter with Logging with FilterConfig with ApiResponse {

  override def apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {
    implicit val req: Request[String] = RequestBuilder.buildRequest[String](rh, "")

    val shuttered = System.getProperty("shuttered", "false").toBoolean

    requestMethodMatches -> requestPathMatches match {
      case (true, true) => f(rh)
      case (_,_)        => if(shuttered) {
        logger.warn("Service is shuttered")
        withFutureJsonResponseBody(SERVICE_UNAVAILABLE, JsString("Service is unavailable, please try again later")) { json =>
          Future(ServiceUnavailable(json))
        }
      } else {
        f(rh)
      }
    }
  }
}
