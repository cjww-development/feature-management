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

import com.typesafe.config.ConfigFactory
import play.sbt.PlayImport.guice
import sbt.Keys.javaOptions
import scoverage.ScoverageKeys

import scala.util.Try

val appName = "feature-management"

val btVersion: String = Try(ConfigFactory.load.getString("version")).getOrElse("0.1.0-local")

lazy val mainDeps: Seq[ModuleID] = Seq(
  guice
)

lazy val testDeps: Seq[ModuleID] = Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0"   % Test,
  "org.mockito"            %  "mockito-core"       % "3.11.0"  % Test,
  "org.scalatestplus"      %% "mockito-3-4"        % "3.2.9.0" % Test
)

lazy val deps = mainDeps ++ testDeps

lazy val scoverageSettings = Seq(
  ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;/.data/..*;views.*;models.*;.*(AuthService|BuildInfo|Routes).*",
  ScoverageKeys.coverageMinimumStmtTotal := 80,
  ScoverageKeys.coverageFailOnMinimum    := false,
  ScoverageKeys.coverageHighlighting     := true
)

lazy val library = Project(appName, file("."))
  .enablePlugins(PlayScala)
  .settings(scoverageSettings:_*)
  .settings(
    version                              :=  btVersion,
    scalaVersion                         :=  "2.13.6",
    semanticdbEnabled                    :=  true,
    semanticdbVersion                    :=  scalafixSemanticdb.revision,
    organization                         :=  "dev.cjww.libs",
    libraryDependencies                  ++= deps,
    githubTokenSource                    := (if (Try(ConfigFactory.load.getBoolean("local")).getOrElse(true)) {
      TokenSource.GitConfig("github.token")
    } else {
      TokenSource.Environment("GITHUB_TOKEN")
    }),
    githubOwner                          :=  "cjww-development",
    githubRepository                     :=  appName,
    scalacOptions                        ++= Seq(
      "-unchecked",
      "-deprecation",
      "-Wunused"
    ),
    Test / testOptions                   +=  Tests.Argument("-oF"),
    Test / javaOptions                   :=  Seq(
      "-Ddata-security.key=testKey",
      "-Ddata-security.salt=testSalt",
      "-Dmicroservice.external-services.admin-frontend.application-id=testAppId"
    )
  )
