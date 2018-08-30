package be.wegenenverkeer.mosaic.infrastructure

import _root_.slick.jdbc.JdbcBackend.DatabaseDef
import be.wegenenverkeer.mosaic.domain.service.{DataloaderService, VerkeersbordenService}

trait HappyNoDbDepsModule {

  def dbOpt: Option[DatabaseDef] = None

  def dataloaderServiceOpt: Option[DataloaderService] = None

  def verkeersbordenServiceOpt: Option[VerkeersbordenService] = None

}
