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

import com.typesafe.config.ConfigFactory
import scoverage.ScoverageKeys

import scala.util.{Failure, Success, Try}

val appName = "feature-management"

val btVersion: String = Try(ConfigFactory.load.getString("version")) match {
  case Success(ver) => ver
  case Failure(_)   => "0.1.0"
}

lazy val scoverageSettings = Seq(
  ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;models/.data/..*;config.*;utils.*;views.*;.*(AuthService|BuildInfo|Routes).*",
  ScoverageKeys.coverageMinimum          := 80,
  ScoverageKeys.coverageFailOnMinimum    := false,
  ScoverageKeys.coverageHighlighting     := true
)

val dependencies: Seq[ModuleID] = Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2"   % Test,
  "org.mockito"             % "mockito-core"       % "2.18.3"  % Test
)

lazy val library = Project(appName, file("."))
  .enablePlugins(PlayScala)
  .settings(scoverageSettings)
  .settings(
    version                                       :=  btVersion,
    scalaVersion                                  :=  "2.12.6",
    organization                                  :=  "com.cjww-dev.libs",
    resolvers                                     ++= Seq(
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      "cjww-dev"            at "http://dl.bintray.com/cjww-development/releases"
    ),
    libraryDependencies                           ++= dependencies,
    bintrayOrganization                           :=  Some("cjww-development"),
    bintrayReleaseOnPublish    in ThisBuild       :=  true,
    bintrayRepository                             :=  "releases",
    bintrayOmitLicense                            :=  true
  )