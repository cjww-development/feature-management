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

import scala.util.Try

val appName = "feature-management"

val btVersion: String = Try(ConfigFactory.load.getString("version")).getOrElse("0.1.0")

lazy val mainDeps: Seq[ModuleID] = Seq(
  guice
)

lazy val testDeps: Seq[ModuleID] = Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
  "org.mockito"            %  "mockito-core"       % "3.3.3" % Test
)

lazy val deps = mainDeps ++ testDeps

lazy val library = Project(appName, file("."))
  .enablePlugins(PlayScala)
  .settings(
    version                                       :=  btVersion,
    scalaVersion                                  :=  "2.13.1",
    organization                                  :=  "com.cjww-dev.libs",
    resolvers                                     ++= Seq(
      "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
      "cjww-dev"            at "https://dl.bintray.com/cjww-development/releases"
    ),
    libraryDependencies                           ++= deps,
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