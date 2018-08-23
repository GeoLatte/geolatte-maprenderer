package be.wegenenverkeer.mosaic.infrastructure

import _root_.slick.jdbc.JdbcBackend.DatabaseDef
import be.wegenenverkeer.mosaic.domain.service.{DataloaderService, VerkeersbordenService}

trait HappyDbDepsModule {

  def db: DatabaseDef

  def dataloaderService: DataloaderService

  def verkeersbordenService: VerkeersbordenService

  def dbOpt: Option[DatabaseDef] = Some(db)

  def dataloaderServiceOpt: Option[DataloaderService] = Some(dataloaderService)

  def verkeersbordenServiceOpt: Option[VerkeersbordenService] = Some(verkeersbordenService)

}
