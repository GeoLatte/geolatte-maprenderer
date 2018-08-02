import BuildSettings._
import Dependencies._

scalaVersion in ThisBuild := "2.12.6"

organization in ThisBuild := "be.wegenenverkeer"

play.sbt.PlayImport.PlayKeys.playDefaultPort := 8080


lazy val root = Project(
  id = "mosaic",
  base = file(".")
).settings(projSettings(mainDeps) ++ Seq(
    routesGenerator := play.routes.compiler.InjectedRoutesGenerator
  )
).configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(//voor buildinfoplugin
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "be.wegenenverkeer.mosaic"
  )
  .enablePlugins(PlayScala, SbtWeb, JavaServerAppPackaging, SystemdPlugin, BuildInfoPlugin)
  .dependsOn(mosaicApiScala)
  .aggregate(mosaicApi, mosaicApiScala, mosaicApiJava)
  .disablePlugins(PlayLayoutPlugin)

parallelExecution in IntegrationTest := false

buildInfoOptions += BuildInfoOption.BuildTime
buildInfoOptions += BuildInfoOption.ToJson


// RAML API Spec=================================
lazy val ramlSettings = Seq(
  scramlBaseDir in scraml in Compile := file("modules/mosaic-api/src/main/resources").absolutePath,
  scramlRamlApi in scraml in Compile := "be/wegenenverkeer/api/mosaic/mosaic-api.raml"
)

lazy val mosaicApi = Project(
  id = "mosaic-api",
  base = file("modules/mosaic-api")
).settings(projSettings())

lazy val mosaicApiScala = Project(
  id = "mosaic-api-scala",
  base = file("modules/mosaic-api-scala")
).settings(ramlSettings ++ projSettings() ++
    Seq(
      scramlLanguage in scraml in Compile := "scala",
      libraryDependencies ++= scramlDependencies
    )
)

lazy val mosaicApiJava = Project(
  id = "mosaic-api-java",
  base = file("modules/mosaic-api-java")
).settings(ramlSettings ++ projSettings() ++
    Seq(
      scramlLanguage in scraml in Compile := "java",
      libraryDependencies ++= scramlDependencies
    ) ++
    Seq(
      crossPaths := false,
      autoScalaLibrary := false,
    )
)
