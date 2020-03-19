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

package com.cjwwdev.shuttering.filters

import org.slf4j.{Logger, LoggerFactory}
import play.api.http.Status.SERVICE_UNAVAILABLE
import play.api.http.Writeable
import play.api.mvc.Results.ServiceUnavailable
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.Future

trait ShutteringFilter[T] extends Filter with FilterConfig {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def shutterResponse(statusCode: Int)(implicit rh: RequestHeader): T

  implicit val writer: Writeable[T]

  val appName: String

  override def apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {
    val shuttered = System.getProperty("shuttered", "false").toBoolean

    requestMethodMatches(rh) -> requestPathMatches(rh) match {
      case (true, true) => f(rh)
      case (_, _) => if(shuttered) {
        logger.warn(s"[ShutteringFilter] - $appName is currently shuttered")
        Future.successful(ServiceUnavailable(shutterResponse(SERVICE_UNAVAILABLE)(rh)))
      } else {
        f(rh)
      }
    }
  }
}
