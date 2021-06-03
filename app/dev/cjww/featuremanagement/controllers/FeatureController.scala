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

package dev.cjww.featuremanagement.controllers

import dev.cjww.featuremanagement.models.Features
import dev.cjww.featuremanagement.services.FeatureService
import play.api.libs.json.{JsString, JsValue, Json}
import play.api.mvc._

import scala.util.{Failure, Success, Try}

trait FeatureController extends BaseController {

  val features: Features

  val featureService: FeatureService

  def validateAdminCall(f: RequestHeader => Result): Action[AnyContent]

  def jsonResponse(status: Int, body: JsValue)(f: JsValue => Result)(implicit rh: RequestHeader): Result

  def getState(featureName: String): Action[AnyContent] = validateAdminCall { implicit req =>
    featureService.getState(featureName).fold(NoContent) { feat =>
      jsonResponse(OK, Json.toJson(feat)) { json =>
        Ok(json)
      }
    }
  }

  def getAllStates(): Action[AnyContent] = validateAdminCall { implicit req =>
    featureService.getAllStates(features) match {
      case x@_::_ => jsonResponse(OK, Json.toJson(x))(json => Ok(json))
      case Nil    => NoContent
    }
  }

  def setState(featureName: String, state: String): Action[AnyContent] = validateAdminCall { implicit req =>
    Try(state.toBoolean) match {
      case Success(bool) =>
        val setState = featureService.setState(featureName, bool)
        val (status, body) = if(bool == setState) {
          OK -> Json.obj(featureName -> setState)
        } else {
          INTERNAL_SERVER_ERROR -> JsString(s"There was a problem setting the state for feature $featureName")
        }

        jsonResponse(status, body) {
          json => Status(status)(json)
        }
      case Failure(_) => jsonResponse(BAD_REQUEST, JsString(s"Provide a boolean state (true or false); found ${state}")) {
        json => BadRequest(json)
      }
    }
  }
}
