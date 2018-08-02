package be.wegenenverkeer.mosaic.test

import java.net.URL

import be.wegenenverkeer.api.mosaic.MosaicApi
import be.wegenenverkeer.api.mosaic.dsl.scalaplay.client.ClientConfig
import be.wegenenverkeer.mosaic.infrastructure.SlickPgProfile.api._
import be.wegenenverkeer.mosaic.infrastructure.apploader.AppLoader
import play.api.{ApplicationLoader, Environment}
import play.core.ApplicationProvider
import play.core.server.{AkkaHttpServer, ServerConfig}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}
import scala.language.postfixOps
import scala.sys.process._
import scala.util.Try

trait EmbeddedApp {

  val clientRequestTimeout = 1000 * 10 // ms

  val acceptanceTestUrlOpt = sys.env.get("ACCEPTANCE_TEST_URL")

  val mosaicUrl: URL = {
    val contextPath = "po"
    if (acceptanceTestUrlOpt.exists(_.nonEmpty)) {
      new URL(new URL(acceptanceTestUrlOpt.get), contextPath)
    } else {
      new URL(new URL("http://localhost:8999/"),  contextPath)
    }
  }

  def inEmbeddedMosaic(body: MosaicApi => Unit): Unit = {
    if (acceptanceTestUrlOpt.exists(_.nonEmpty)) {
      inRemoteMosaic(body)
    } else {
      _inEmbeddedMosaic(body)
    }
  }

  def inRemoteMosaic(body: MosaicApi => Unit): Unit = {
    // zorgt dat de scripts uitvoerbaar zijn
    println(("find infrastructuurscripts/bamboo/it/ -type f -print0" #| "xargs -0 chmod +x").!!)

    println("infrastructuurscripts/bamboo/it/stop_service mosaic".!!)
    println("infrastructuurscripts/bamboo/it/recreate_db".!!)
    println("infrastructuurscripts/bamboo/it/start_service mosaic".!!)

    val mosaicApi = MosaicApi(
      url = mosaicUrl,
      config = ClientConfig(requestTimeout = clientRequestTimeout),
      defaultHeaders =
        Map(
          "Accept" -> "application/json"
          // De "Content-Type" -> "application/json" header mag niet by default gezet worden omdat Play bij
          // een lege post() de (afwezige) body dan naar JSON probeert om te zetten, wat faalt.
        )
    )

    body(mosaicApi)

  }

  def _inEmbeddedMosaic(body: MosaicApi => Unit): Unit = {

    // Zie: test/resources/database.conf Alleen met deze 'alternatieve' configuratie krijgen we hier een geldige DB connectie.
    val db = Database.forConfig("slick.dbs.testdbsetup")
    recreateDb(db)
    db.close()

    val context = ApplicationLoader.createContext(Environment.simple())
    val application = new AppLoader().load(context)

    val nettyShutdownPromise = Promise[Boolean]()

    val akkaHttpServer = new AkkaHttpServer(
      AkkaHttpServer.Context(
      config = ServerConfig(address = mosaicUrl.getHost, port = Some(mosaicUrl.getPort)),
        appProvider = ApplicationProvider(application),
      actorSystem = application.actorSystem,
        materializer = application.materializer,
        stopHook = () => Future.successful(nettyShutdownPromise.complete(Try(true)))
      )
    )

    val api = MosaicApi(
      url = mosaicUrl,
      config = ClientConfig(requestTimeout = clientRequestTimeout),
      defaultHeaders =
        Map(
          "Accept" -> "application/json"
          // De "Content-Type" -> "application/json" header mag niet by default gezet worden omdat Play bij
          // een lege post() de (afwezige) body dan naar JSON probeert om te zetten, wat faalt.
        )
    )

    try {
      body(api)
    }
    finally {
      Await.result(application.stop(), 10.seconds)
      api.close() // Als api niet meer gebruikt wordt moeten we de client connecties afsluiten.
      akkaHttpServer.stop()
      Await.result(nettyShutdownPromise.future, 10.seconds)
      println(s"netty stopped")
    }

  }

  private def recreateDb(db: Database): Unit = {

    import resource._
    managed(db.createSession()).map { session =>
      session.prepareStatement(
        """drop schema public cascade;
           create schema public;
           create extension if not exists hstore;""").executeUpdate()
    }.either.right

  }
}
