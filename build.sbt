import BuildSettings._
import Dependencies._

scalaVersion in ThisBuild := "2.12.6"

organization in ThisBuild := "be.wegenenverkeer"

play.sbt.PlayImport.PlayKeys.playDefaultPort := 8080

lazy val root = Project(
  id   = "mosaic",
  base = file(".")
).settings(projSettings(mainDeps) ++ Seq(
    routesGenerator := play.routes.compiler.InjectedRoutesGenerator
  ))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings( //voor buildinfoplugin
            buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
            buildInfoPackage := "be.wegenenverkeer.mosaic")
  .enablePlugins(PlayScala, SbtWeb, JavaServerAppPackaging, SystemdPlugin, BuildInfoPlugin)
  .dependsOn(
    mosaicApiScala,
    dataloaderApiScala,
    verkeersbordenApiScala,
    geowebcacheApiScala,
    geolatteGeom,
    geolatteGeojson,
    geolatteMaprenderer,
    geolatteMapserver,
    geolatteMapserverImageops,
    geolatteMapserverProtocols,
    geolatteMapserverConfig,
    geolatteMapserverRxHttpFeatureSource
  )
  .aggregate(
    mosaicApi,
    mosaicApiScala,
    dataloaderApiScala,
    verkeersbordenApiScala,
    geowebcacheApiScala,
    mosaicApiJava,
    geolatteGeom,
    geolatteGeojson,
    geolatteMaprenderer,
    geolatteMapserver,
    geolatteMapserverImageops,
    geolatteMapserverProtocols,
    geolatteMapserverConfig,
    geolatteMapserverRxHttpFeatureSource
  )
  .disablePlugins(PlayLayoutPlugin)

parallelExecution in IntegrationTest := false

buildInfoOptions += BuildInfoOption.BuildTime
buildInfoOptions += BuildInfoOption.ToJson

// RAML API Spec=================================
lazy val ramlSettings = Seq(
  scramlBaseDir in scraml in Compile := file("modules/mosaic-api/src/main/resources").absolutePath,
  scramlRamlApi in scraml in Compile := "be/wegenenverkeer/api/mosaic/mosaic-api.raml"
)

lazy val dataloaderRamlSettings = Seq(
  scramlBaseDir in scraml in Compile := file("modules/mosaic-api/src/main/resources").absolutePath,
  scramlRamlApi in scraml in Compile := "be/wegenenverkeer/api/dataloader/dataloader-api.raml"
)

lazy val verkeersbordenRamlSettings = Seq(
  scramlBaseDir in scraml in Compile := file("modules/mosaic-api/src/main/resources").absolutePath,
  scramlRamlApi in scraml in Compile := "be/wegenenverkeer/api/verkeersborden/verkeersborden-api.raml"
)

lazy val geowebcacheRamlSettings = Seq(
  scramlBaseDir in scraml in Compile := file("modules/mosaic-api/src/main/resources").absolutePath,
  scramlRamlApi in scraml in Compile := "be/wegenenverkeer/api/geowebcache/geowebcache-api.raml"
)

lazy val mosaicApi = Project(
  id   = "mosaic-api",
  base = file("modules/mosaic-api")
).settings(projSettings())

lazy val mosaicApiScala = Project(
  id   = "mosaic-api-scala",
  base = file("modules/mosaic-api-scala")
).settings(
  ramlSettings ++ projSettings() ++
    Seq(
      scramlLanguage in scraml in Compile := "scala",
      libraryDependencies ++= scramlDependencies
    ))

lazy val mosaicApiJava = Project(
  id   = "mosaic-api-java",
  base = file("modules/mosaic-api-java")
).settings(
  ramlSettings ++ projSettings() ++
    Seq(
      scramlLanguage in scraml in Compile := "java",
      libraryDependencies ++= scramlDependencies
    ) ++
    Seq(
      crossPaths := false,
      autoScalaLibrary := false,
    ))

lazy val dataloaderApiScala = Project(
  id   = "dataloader-api-scala",
  base = file("modules/dataloader-api-scala")
).settings(
  dataloaderRamlSettings ++ projSettings() ++
    Seq(
      scramlLanguage in scraml in Compile := "scala",
      libraryDependencies ++= scramlDependencies
    ))

lazy val verkeersbordenApiScala = Project(
  id   = "verkeersborden-api-scala",
  base = file("modules/verkeersborden-api-scala")
).settings(
  verkeersbordenRamlSettings ++ projSettings() ++
    Seq(
      scramlLanguage in scraml in Compile := "scala",
      libraryDependencies ++= scramlDependencies
    ))
  .dependsOn(geolatteGeom)

lazy val geowebcacheApiScala = Project(
  id   = "geowebcache-api-scala",
  base = file("modules/geowebcache-api-scala")
).settings(
  geowebcacheRamlSettings ++ projSettings() ++
    Seq(
      scramlLanguage in scraml in Compile := "scala",
      libraryDependencies ++= scramlDependencies
    ))

lazy val geolatteGeom = Project(
  id   = "geolatte-geom",
  base = file("modules/geolatte-geom/geom")
).settings(
  projSettings(
    Seq(
      "com.vividsolutions" % "jts-core"  % "1.14.0",
      "org.slf4j"          % "slf4j-api" % "1.6.1"
    ) ++ geomTestDeps))

lazy val geolatteGeojson = Project(
  id   = "geolatte-geojson",
  base = file("modules/geolatte-geom/json")
).settings(
    projSettings(
      Seq(
        jacksonDatabindDep,
        "junit" % "junit" % "4.4" % Test
      )))
  .dependsOn(geolatteGeom)

lazy val geolatteMaprenderer = Project(
  id   = "geolatte-maprenderer",
  base = file("modules/geolatte-maprenderer")
).settings(projSettings(Seq(
    "org.apache.httpcomponents" % "httpclient"       % "4.5.5",
    "org.apache.xmlgraphics"    % "batik-transcoder" % "1.9.1",
    "org.apache.xmlgraphics"    % "batik-codec"      % "1.9.1",
    "org.apache.xmlgraphics"    % "batik-svg-dom"    % "1.9.1",
    ehCache,
    sldSchemaDep,
    wmsSchemaDep
  ) ++ maprendererTestDeps))
  .dependsOn(geolatteGeom)

lazy val geolatteMapserver = Project(
  id   = "geolatte-mapserver",
  base = file("modules/geolatte-mapserver/mapserver")
).settings(projSettings(Seq("io.reactivex" % "rxjava" % "1.2.4") ++ mapserverTestDeps))
  .dependsOn(geolatteGeom, geolatteMaprenderer)

lazy val geolatteMapserverImageops = Project(
  id   = "mapserver-imageops",
  base = file("modules/geolatte-mapserver/mapserver-imageops")
).settings(projSettings(mapserverTestDeps))
  .dependsOn(geolatteMapserver)

lazy val geolatteMapserverProtocols = Project(
  id   = "mapserver-protocols",
  base = file("modules/geolatte-mapserver/mapserver-protocols")
).settings(projSettings(Seq(wmsSchemaDep, wmtsSchemaDep) ++ mapserverTestDeps))
  .dependsOn(geolatteMapserver)

lazy val geolatteMapserverConfig = Project(
  id   = "mapserver-config",
  base = file("modules/geolatte-mapserver/mapserver-config")
).settings(projSettings(Seq(typesafeConfigDep) ++ mapserverTestDeps))
  .dependsOn(geolatteMapserver)

lazy val geolatteMapserverRxHttpFeatureSource = Project(
  id   = "mapserver-rxhttpfeaturesource",
  base = file("modules/geolatte-mapserver/mapserver-rxhttpfeaturesource")
).settings(
    projSettings(
      Seq(
        jacksonDatabindDep,
        rxhttpclientJavaDep,
        typesafeConfigDep,
        "org.antlr" % "stringtemplate" % "4.0.2"
      )
        ++ mapserverTestDeps
    )
  )
  .dependsOn(geolatteMapserver, geolatteGeom, geolatteGeojson)
