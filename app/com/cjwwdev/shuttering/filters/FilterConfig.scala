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

import play.api.http.HttpVerbs
import play.api.mvc.RequestHeader

trait FilterConfig {
  protected val shutterOnOffRoute       = """(.*)\/service-shuttering\/(true|false)"""
  protected val getStateRoute           = """(.*)\/service-shuttering\/state"""

  protected val getSpecificFeatureRoute = """(.*)\/feature\/(.*)\/state"""
  protected val getFeaturesRoute        = "/features"
  protected val updateFeatureRoute      = """(.*)\/feature\/(.*)\/state\/(true|false)"""

  protected val assetsRoutes            = "/assets/"

  def requestPathMatches(implicit rh: RequestHeader): Boolean = {
    rh.path.matches(shutterOnOffRoute) | rh.path.matches(getStateRoute) |
      rh.path.matches(getSpecificFeatureRoute) | rh.path.contains(getFeaturesRoute) |
        rh.path.matches(updateFeatureRoute) | rh.path.contains(assetsRoutes)
  }

  def requestMethodMatches(implicit rh: RequestHeader): Boolean = {
    rh.method.eq(HttpVerbs.GET) | rh.method.eq(HttpVerbs.POST) | rh.method.eq(HttpVerbs.PATCH)
  }
}
