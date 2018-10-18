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

package com.cjwwdev.featuremanagement.controllers

import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.featuremanagement.models.Features
import com.cjwwdev.featuremanagement.services.FeatureService
import com.cjwwdev.http.headers.HttpHeaders
import com.typesafe.config.ConfigFactory
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc._

import scala.util.{Failure, Success, Try}

class DefaultFeatureController @Inject()(val controllerComponents: ControllerComponents,
                                         val config: ConfigurationLoader,
                                         val featureService: FeatureService) extends FeatureController {
  override val features: Features = getClass
    .getClassLoader
    .loadClass(config.get[String]("features.definition"))
    .asInstanceOf[Features]

  override val appId: String = config.getServiceId(config.get[String]("appName"))
}

trait FeatureController extends BaseController with HttpHeaders {

  val features: Features

  val featureService: FeatureService

  def getState(featureName: String): Action[AnyContent] = validateAdminCall {
    Ok(Json.toJson(featureService.getState(featureName)))
  }

  def getAllStates(): Action[AnyContent] = validateAdminCall {
    Ok(Json.toJson(featureService.getAllStates(features)))
  }

  def setState(featureName: String, state: String): Action[AnyContent] = validateAdminCall {
    Try(state.toBoolean) match {
      case Success(bool) =>
        val setState = featureService.setState(featureName, bool)
        if(state.toBoolean == setState) Ok(Json.obj(featureName -> state)) else InternalServerError
      case Failure(_) => BadRequest
    }
  }

  private def validateAdminCall(f: => Result): Action[AnyContent] = Action { implicit request =>
    constructHeaderPackageFromRequestHeaders.fold[Result](NotFound) { headers =>
      if(ConfigFactory.load.getString("microservices.external-services.admin-frontend.application-id") == headers.appId) {
        f
      } else {
        Forbidden
      }
    }
  }
}
