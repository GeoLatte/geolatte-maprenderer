package be.wegenenverkeer.mosaic.infrastructure.apploader

import java.net.URL

import be.wegenenverkeer.atomium.format.Url
import be.wegenenverkeer.metrics.{Metrics, MetricsHolder}
import be.wegenenverkeer.metrics.controller.MetricsController
import be.wegenenverkeer.metrics.filter.MetricFilter
import be.wegenenverkeer.mosaic.AppMachtigingen
import be.wegenenverkeer.mosaic.AppMachtigingen._
import be.wegenenverkeer.mosaic.api.{AppPoAuth, WebApplicationController, WmsController}
import be.wegenenverkeer.mosaic.infrastructure.AppAkkaModule
import be.wegenenverkeer.playevolutions._
import be.wegenenverkeer.poauth.domain.model._
import be.wegenenverkeer.poauth.infrastructure.{MockPoProvider, PoClient}
import be.wegenenverkeer.restfailure.errorhandler.RestExceptionAwareErorHandler
import slick.basic.{BasicProfile, DatabaseConfig}
import slick.jdbc.JdbcBackend.DatabaseDef
import slick.jdbc.JdbcProfile
import be.wegenenverkeer.mosaic.infrastructure._
import be.wegenenverkeer.playfilters.accesslog.PlayLoggingFilter
import be.wegenenverkeer.slick3._
import com.softwaremill.macwire._
import controllers.{Assets, AssetsConfigurationProvider, AssetsMetadataProvider}
import org.webjars.play.WebJarAssets
import play.api.ApplicationLoader.Context
import play.api.db.slick.{DatabaseConfigProvider, DbName, SlickComponents, SlickModule}
import play.api.inject.ApplicationLifecycle
import play.api.libs.logback.LogbackLoggerConfigurator
import play.api.mvc.{ControllerComponents, DefaultControllerComponents, EssentialFilter}
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponents, BuiltInComponentsFromContext}
import play.filters.gzip.GzipFilter
import router.Routes

import scala.concurrent.Future
import scala.util.Try

class AppLoader extends ApplicationLoader {

  def load(context: Context): Application = {

    val app = new BuiltInComponentsFromContext(context) with MacWireEvolutionsModule with AppComponents

    app.applicationLifecycle.addStopHook { () =>
      import scala.concurrent.ExecutionContext.Implicits.global
      for {
        _ <- app.actorSystem.terminate()
        _ <- app.actorSystem.whenTerminated
        _ <- Future.fromTry(Try(app.db.close()))
        _ <- Future.fromTry(Try(app.poProvider.close()))
      } yield ()
    }
    app.application
  }

}

trait AppComponents
    extends BuiltInComponents
    with AppContext
    with AppModule
    with AtomFeedModule
    with SlickComponents
    with HappyModule
    with AppAkkaModule {

  def controllerComponents: ControllerComponents

  lazy val assetsConfiguration = wire[AssetsConfigurationProvider].get
  lazy val assetsMetadata = wire[AssetsMetadataProvider].get
  lazy val assets: Assets = new controllers.Assets(httpErrorHandler,assetsMetadata)
  lazy val webjarAssetsController = wire[WebJarAssets]

  lazy val router: Router = {
    val dummyOmRoutesTeDoenWerken: String = "dummyOmRoutesTeDoenWerken"

     wire[Routes] withPrefix configuration.getOptional[String]("play.http.context").getOrElse("mosaic")
  }

  lazy val webApplicationController = wire[WebApplicationController]
  lazy val wmsController = wire[WmsController]

  lazy val poProvider = if (configuration.getOptional[Boolean]("poauth.dangerous.mockProvider.enabled").getOrElse(false)) {
    new MockPoProvider(Map(applicatieUser -> GebruikerMachtigingen(
      gebruiker = Gebruiker(voId = VoId(applicatieUser), voornaam = "mosaic", naam = "Systeem", actief = true, organisatie = None, functie = None),
      machtigingen = Set(
        Machtiging(toepassing = toepassingCode, rol = Rol(adminRol, adminRol), toelating = None, organisatie = None)
      )
    )))
  } else {
    new PoClient(configuration.getOptional[String]("po.url").getOrElse(sys.error("po.url moet gespecifieerd zijn")), AppMachtigingen.toepassingCode)
  }

  lazy val appPoAuth: AppPoAuth = new AppPoAuth(poProvider)

  lazy val db: DatabaseDef = dbConfigProvider.get[JdbcProfile].db.asInstanceOf[DatabaseDef]

  lazy val dbRunner: DbRunner = new be.wegenenverkeer.slick3.DbRunner(db, be.wegenenverkeer.mosaic.infrastructure.SlickPgProfile, Some(MetricsHolder.metricRegistry))

  lazy val feedBaseUrl = new Url(configuration.getOptional[String]("http.url").getOrElse(sys.error("http.url moet geconfigureerd worden")))

  override lazy val httpErrorHandler =
    new RestExceptionAwareErorHandler(environment, configuration, sourceMapper, Some(router))

  lazy val dbConfigProvider = new DatabaseConfigProvider {
    override def get[P <: BasicProfile]: DatabaseConfig[P] = {
      val defaultDbName = configuration.underlying.getString(SlickModule.DefaultDbName)
      slickApi.dbConfig[P](DbName(defaultDbName))
    }
  }

  new LogbackLoggerConfigurator() {

    override def configure(properties: Map[String, String], config: Option[URL]) =
      super.configure(properties - "application.home", config)

  }.configure(environment)

  def applicationLifecycle: ApplicationLifecycle

  val userMetrics = wire[Metrics]

  lazy val metricsController = wire[MetricsController]

  // De REST metric filter zal de durations van alle rest calls publiceren naar Prometheus.
  lazy val metricFilter: EssentialFilter = new MetricFilter(_ => true)

  // Vergeet niet om de ACCESSFILE logback appender toe te voegen in logback.xml voor de 'requests' logger.
  lazy val accessLogFilter: EssentialFilter = wire[PlayLoggingFilter]

  lazy val gzipFilter = new GzipFilter()

  override lazy val httpFilters = List[EssentialFilter](metricFilter, accessLogFilter, gzipFilter)

}
