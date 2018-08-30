package be.wegenenverkeer.mosaic.domain.model

import io.funcqrs._

trait MosaicCommand extends CommandIdFacet {
  val id = CommandId()
  def gebruiker: String
}

trait MosaicEvent extends EventWithCommandId {
  def tags: Set[Tag]
}
