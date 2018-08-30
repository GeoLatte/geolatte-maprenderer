package be.wegenenverkeer.mosaic.util

import play.api.Logger

trait Logging {

  val logger: Logger = Logger(this.getClass.getName)

  def logger(loggingLabel: String): Logger = Logger(s"${this.getClass.getName}.$loggingLabel")
}
