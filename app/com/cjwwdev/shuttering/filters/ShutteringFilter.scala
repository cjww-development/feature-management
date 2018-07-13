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

import com.cjwwdev.frontendUI.builders.NavBarLinkBuilder
import com.cjwwdev.logging.Logging
import com.cjwwdev.request.RequestBuilder
import com.cjwwdev.views.html.templates.errors.MaintenanceView
import play.api.http.HttpVerbs
import play.api.i18n.{Lang, Langs, MessagesApi}
import play.api.mvc.Results.ServiceUnavailable
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ShutteringFilter extends Filter with Logging with RequestBuilder {
  val langs: Langs
  implicit val messages: MessagesApi

  implicit val pageLinks: Seq[NavBarLinkBuilder]
  implicit val navBarRoutes: Map[String, Call]

  private val shutterRoute   = "/service-shuttering/true"
  private val unshutterRoute = "/service-shuttering/false"

  override def apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {
    implicit val lang: Lang           = langs.preferred(rh.acceptLanguages)
    implicit val req: Request[String] = buildNewRequest[String](rh, "")

    if(rh.method == HttpVerbs.PATCH & (rh.path.contains(shutterRoute) | rh.path.contains(unshutterRoute))) {
      f(rh)
    } else {
      if(System.getProperty("shuttered").toBoolean) {
        Future(ServiceUnavailable(MaintenanceView()))
      } else {
        f(rh)
      }
    }
  }
}