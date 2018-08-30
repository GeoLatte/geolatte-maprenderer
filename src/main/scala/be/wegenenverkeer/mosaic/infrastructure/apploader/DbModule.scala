package be.wegenenverkeer.mosaic.infrastructure.apploader

import be.wegenenverkeer.metrics.MetricsHolder
import be.wegenenverkeer.slick3.DbRunner
import play.api.db.slick.{DatabaseConfigProvider, DbName, SlickComponents, SlickModule}
import slick.basic.{BasicProfile, DatabaseConfig}
import slick.jdbc.JdbcBackend.DatabaseDef
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

trait DbModule extends SlickComponents {

  implicit def executionContext: ExecutionContext

  lazy val db: DatabaseDef = dbConfigProvider.get[JdbcProfile].db.asInstanceOf[DatabaseDef]

  lazy val dbRunner: DbRunner =
    new be.wegenenverkeer.slick3.DbRunner(db, be.wegenenverkeer.mosaic.infrastructure.SlickPgProfile, Some(MetricsHolder.metricRegistry))

  lazy val dbConfigProvider: DatabaseConfigProvider = new DatabaseConfigProvider {
    override def get[P <: BasicProfile]: DatabaseConfig[P] = {
      val defaultDbName = configuration.underlying.getString(SlickModule.DefaultDbName)
      slickApi.dbConfig[P](DbName(defaultDbName))
    }
  }

}
