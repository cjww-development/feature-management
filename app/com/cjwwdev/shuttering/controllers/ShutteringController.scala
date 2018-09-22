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

import com.cjwwdev.http.headers.HttpHeaders
import com.cjwwdev.responses.ApiResponse
import com.typesafe.config.ConfigFactory
import javax.inject.Inject
import play.api.libs.json.JsString
import play.api.mvc._

class DefaultShutteringController @Inject()(val controllerComponents: ControllerComponents) extends ShutteringController

trait ShutteringController extends BaseController with HttpHeaders with ApiResponse {

  def shutter(): Action[AnyContent] = validateAdminCall { implicit request =>
    System.setProperty("shuttered", "true")
    NoContent
  }

  def unshutter(): Action[AnyContent] = validateAdminCall { implicit request =>
    System.setProperty("shuttered", "false")
    NoContent
  }

  def getShutterState(): Action[AnyContent] = validateAdminCall { implicit request =>
    withJsonResponseBody(OK, JsString(System.getProperty("shuttered", "false"))) { json =>
      Ok(json)
    }
  }

  private def validateAdminCall(f: Request[_] => Result): Action[AnyContent] = Action { implicit request =>
    constructHeaderPackageFromRequestHeaders.fold[Result](NotFound) { headers =>
      if(ConfigFactory.load.getString("microservices.external-services.admin-frontend.application-id") == headers.appId) {
        f(request)
      } else {
        Forbidden
      }
    }
  }
}