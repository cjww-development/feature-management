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

import scala.util.{Failure, Success, Try}
import play.sbt.PlayImport.guice
import scoverage.ScoverageKeys

val appName = "feature-management"

val btVersion: String = Try(ConfigFactory.load.getString("version")) match {
  case Success(ver) => ver
  case Failure(_)   => "0.1.0"
}

lazy val scoverageSettings = Seq(
  ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;models/.data/..*;com.cjwwdev.modules.*;com.cjwwdev.shuttering.filters.*;.*(AuthService|BuildInfo|Routes).*",
  ScoverageKeys.coverageMinimum          := 80,
  ScoverageKeys.coverageFailOnMinimum    := false,
  ScoverageKeys.coverageHighlighting     := true
)

val dependencies: Seq[ModuleID] = Seq(
  guice,
  "com.typesafe.play" %  "play_2.12"         % "2.6.20",
  "com.cjww-dev.libs" %  "http-verbs_2.12"   % "3.6.0",
  "com.cjww-dev.libs" %  "frontend-ui_2.12"  % "2.8.2",
  "com.cjww-dev.libs" %% "testing-framework" % "3.2.0"   % Test
)

lazy val library = Project(appName, file("."))
  .enablePlugins(PlayScala)
  .settings(scoverageSettings)
  .settings(
    version                                       :=  btVersion,
    scalaVersion                                  :=  "2.12.7",
    organization                                  :=  "com.cjww-dev.libs",
    resolvers                                     ++= Seq(
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      "cjww-dev"            at "http://dl.bintray.com/cjww-development/releases"
    ),
    libraryDependencies                           ++= dependencies,
    bintrayOrganization                           :=  Some("cjww-development"),
    bintrayReleaseOnPublish    in ThisBuild       :=  true,
    bintrayRepository                             :=  "releases",
    bintrayOmitLicense                            :=  true,
    javaOptions                in Test            :=  Seq(
      "-Ddata-security.key=testKey",
      "-Ddata-security.salt=testSalt",
      "-Dmicroservice.external-services.admin-frontend.application-id=testAppId"
    )
  )