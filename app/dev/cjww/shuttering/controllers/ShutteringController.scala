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

package dev.cjww.shuttering.controllers

import play.api.libs.json.{JsString, JsValue}
import play.api.mvc._


trait ShutteringController extends BaseController {

  def validateAdminCall(f: RequestHeader => Result): Action[AnyContent]

  def jsonResponse(status: Int, body: JsValue)(f: JsValue => Result)(implicit rh: RequestHeader): Result

  def shutter(): Action[AnyContent] = validateAdminCall { _ =>
    System.setProperty("shuttered", "true")
    NoContent
  }

  def unShutter(): Action[AnyContent] = validateAdminCall { _ =>
    System.setProperty("shuttered", "false")
    NoContent
  }

  def getShutterState(): Action[AnyContent] = validateAdminCall { implicit req =>
    jsonResponse(OK, JsString(System.getProperty("shuttered", "false"))) { json =>
      Ok(json)
    }
  }
}