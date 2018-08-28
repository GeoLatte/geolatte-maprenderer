import sbt._

object Dependencies {

  lazy val mainDeps =
    funCqrsDeps ++ macwireDeps ++ slickDeps ++
      awvDeps ++ atomiumDeps ++
      testDeps ++
      loggingDeps ++ otherDeps ++ mosaicApiDeps ++ metricsDependencies ++ Seq(ehCache)

  // ----------------------------------------------------
  // versie van libs die voorkomen in meer dan één group
  val playExtVersion      = "1.0.2"
  val rxHttpClientVersion = "0.6"
  val scalaLoggingVersion = "3.7.2"
  val logbackVersion      = "0.2.3"
  val scalaExtVersion     = "1.0.0"
  val scalaMetricsVersion = "3.5.9"
  val awsVersion          = "1.11.347"
  // ----------------------------------------------------

  val mosaicApiDeps = Seq(
    "be.wegenenverkeer"          %% "rxhttpclient-scala" % rxHttpClientVersion,
    "com.typesafe.scala-logging" %% "scala-logging"      % scalaLoggingVersion
  )

  val loggingDeps = {
    Seq(
      "be.wegenenverkeer"          %% "scala-logging-ext"       % scalaExtVersion,
      "com.typesafe.scala-logging" %% "scala-logging"           % scalaLoggingVersion,
      "ch.qos.logback"             % "logback-classic"          % logbackVersion,
      "net.logstash.logback"       % "logstash-logback-encoder" % "4.11"
    )
  }

  val otherDeps = {
    Seq(
      "com.jsuereth" %% "scala-arm"       % "2.0",
      "nl.grons"     %% "metrics-scala"   % scalaMetricsVersion,
      "com.amazonaws" % "aws-java-sdk-s3" % awsVersion
    )
  }

  val sldSchemaDep        = "org.jvnet.ogc"              % "sld-v_1_1_0-schema"  % "1.0.3"
  val wmsSchemaDep        = "org.jvnet.ogc"              % "wms-v_1_3_0-schema"  % "1.0.3"
  val wmtsSchemaDep       = "org.jvnet.ogc"              % "wmts-v_1_0_0-schema" % "1.1.0"
  val jacksonDatabindDep  = "com.fasterxml.jackson.core" % "jackson-databind"    % "2.9.0"
  val rxhttpclientJavaDep = "be.wegenenverkeer"          % "rxhttpclient-java"   % rxHttpClientVersion
  val typesafeConfigDep   = "com.typesafe"               % "config"              % "1.3.2"
  val ehCache             = "org.ehcache"                % "ehcache"             % "3.5.2"

  val scramlDependencies = Seq(
    "com.ning" % "async-http-client" % "1.9.40",
    // Dit zou prefereerbaar zijn, spijtig genoeg worden deze releases niet meer in sync gehouden
    // "com.typesafe.play" %% "play-json"        % play.core.PlayVersion.current
    "com.typesafe.play" %% "play-json" % "2.6.9"
  )

  //------------------------------------------------------------------------------------------------
  // AWV Play extensions - minimum - gebruikt ook in thor-import
  val poauthVersion      = "2.9.1"
  val restfailureVersion = "1.6.2"

  val awvDepsMin = {
    Seq(
      "be.wegenenverkeer" %% "poauth-base"        % poauthVersion,
      "be.wegenenverkeer" %% "restfailure-play24" % restfailureVersion
    )
  }

  //------------------------------------------------------------------------------------------------
  // AWV Play extensions
  val awvDeps = {

    val appStatusVersion = "3.0.4-SNAPSHOT"
    val hateoasVersion   = "2.0.2"

    Seq(
      "be.wegenenverkeer" %% "play26-evolutions-macwire" % playExtVersion,
      "be.wegenenverkeer" %% "play26-versionedassets"    % playExtVersion,
      "be.wegenenverkeer" %% "play26-json-ext"           % playExtVersion,
      "be.wegenenverkeer" %% "play26-results"            % playExtVersion,
      "be.wegenenverkeer" %% "play26-filters"            % playExtVersion,
      "be.wegenenverkeer" %% "play26-metrics"            % playExtVersion,
      "be.wegenenverkeer" %% "poauth-play26"             % poauthVersion,
      "be.wegenenverkeer" %% "restfailure-base"          % restfailureVersion,
      "be.wegenenverkeer" %% "restfailure-play26"        % restfailureVersion,
      "be.wegenenverkeer" %% "app-status-play26"         % appStatusVersion,
      "be.wegenenverkeer" %% "app-status-ui"             % appStatusVersion,
      "be.wegenenverkeer" %% "app-status-monitor"        % appStatusVersion,
      "be.wegenenverkeer" %% "app-status-metrics"        % appStatusVersion,
      "be.wegenenverkeer" %% "app-status-rood"           % appStatusVersion,
      "be.wegenenverkeer" %% "rxhttpclient-scala"        % rxHttpClientVersion
    )
  }

  //------------------------------------------------------------------------------------------------
  // main test dependencies
  val testDeps = {
    val scalaTestVersion  = "3.0.3"
    val scalaCheckVersion = "1.13.5"

    Seq(
      "org.scalatest"     %% "scalatest"      % scalaTestVersion  % "it,test",
      "be.wegenenverkeer" %% "scala-test-ext" % scalaExtVersion   % "it,test",
      "org.scalacheck"    %% "scalacheck"     % scalaCheckVersion % "it,test"
    )
  }

  val geomTestDeps = {
    Seq(
      "junit"                % "junit"               % "4.4"    % Test,
      "log4j"                % "log4j"               % "1.2.14" % Test,
      "org.slf4j"            % "slf4j-log4j12"       % "1.6.1"  % Test,
      "org.codehaus.jackson" % "jackson-mapper-lgpl" % "1.5.2"  % Test,
      "commons-collections"  % "commons-collections" % "3.2"    % Test,
      "dom4j"                % "dom4j"               % "1.6.1"  % Test,
      "jaxen"                % "jaxen"               % "1.1"    % Test
    )
  }

  val mapserverTestDeps = {
    Seq(
      "junit"                  % "junit"         % "4.12"   % Test,
      "org.slf4j"              % "slf4j-simple"  % "1.7.25" % Test,
      "com.github.tomakehurst" % "wiremock"      % "2.18.0" % Test,
      "org.mockito"            % "mockito-all"   % "1.8.5"  % Test,
      "org.hamcrest"           % "hamcrest-core" % "1.3"   % Test
    )
  }

  val maprendererTestDeps = {
    Seq(
      "junit"       % "junit"         % "4.12"   % Test,
      "log4j"       % "log4j"         % "1.2.14" % Test,
      "org.slf4j"   % "slf4j-log4j12" % "1.6.1"  % Test,
      "org.mockito" % "mockito-all"   % "1.8.5"  % Test
    )
  }

  //------------------------------------------------------------------------------------------------
  // Atomium
  val atomiumDeps = {
    val version        = "1.3.0"
    val jacksonVersion = "2.9.0"

    Seq(
      "be.wegenenverkeer"          %% "atomium-play26"                    % version,
      "be.wegenenverkeer"          %% "atomium-client-scala"              % version,
      "io.reactivex"               %% "rxscala"                           % "0.26.5",
      "be.wegenenverkeer"          %% "atomium-extension-feed-consumer26" % "1.0.3-SNAPSHOT",
      "com.fasterxml.jackson.core" % "jackson-core"                       % jacksonVersion,
      "com.fasterxml.jackson.core" % "jackson-databind"                   % jacksonVersion,
      "com.fasterxml.jackson.core" % "jackson-annotations"                % jacksonVersion
    )
  }

  //------------------------------------------------------------------------------------------------
  // Fun.CQRS and Akka dependencies
  val funCqrsDeps = {

    val akkaVersion    = "2.5.14"
    val funCqrsVersion = "1.0.1"

    Seq(
      "com.typesafe.akka" %% "akka-actor"          % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j"          % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit"        % akkaVersion % "it,test",
      "be.wegenenverkeer" %% "akka-persistence-pg" % "0.7.0",
      "be.wegenenverkeer" %% "fun-cqrs-ext"        % scalaExtVersion,
      "io.strongtyped"    %% "fun-cqrs-akka"       % funCqrsVersion,
      "io.strongtyped"    %% "fun-cqrs-test-kit"   % funCqrsVersion % "it,test"
    )
  }

  //------------------------------------------------------------------------------------------------
  // Prometheus metrics
  val prometheusClientVersion = "0.0.22"

  val metricsDependencies = Seq(
    "io.prometheus" % "simpleclient"            % prometheusClientVersion,
    "io.prometheus" % "simpleclient_common"     % prometheusClientVersion,
    "io.prometheus" % "simpleclient_hotspot"    % prometheusClientVersion,
    "io.prometheus" % "simpleclient_dropwizard" % prometheusClientVersion,
    "io.prometheus" % "simpleclient_common"     % prometheusClientVersion,
    "nl.grons"      %% "metrics-scala"          % scalaMetricsVersion
  )

  //------------------------------------------------------------------------------------------------
  // Macwire
  val macwireDeps = {
    val version = "2.3.0"
    Seq(
      "com.softwaremill.macwire" %% "macros" % version % "provided",
      "com.softwaremill.macwire" %% "util"   % version
    )
  }

  //------------------------------------------------------------------------------------------------
  // Play Dependencies
  val slickDeps = {

    val slickVersion      = "3.2.1"
    val playSlickVersion  = "3.0.2"
    val slickPgVersion    = "0.15.3" // Controleren via https://github.com/tminglei/slick-pg/releases
    val postgresqlVersion = "42.1.4"

    Seq(
      "com.typesafe.slick"  %% "slick"                 % slickVersion,
      "com.typesafe.slick"  %% "slick-hikaricp"        % slickVersion,
      "org.postgresql"      % "postgresql"             % postgresqlVersion,
      "com.typesafe.play"   %% "play-slick"            % playSlickVersion,
      "com.typesafe.play"   %% "play-slick-evolutions" % playSlickVersion,
      "be.wegenenverkeer"   %% "slick3-ext"            % scalaExtVersion,
      "com.github.tminglei" %% "slick-pg"              % slickPgVersion,
      "com.github.tminglei" %% "slick-pg_play-json"    % slickPgVersion //play 2.6 !!!
    )
  }

}
