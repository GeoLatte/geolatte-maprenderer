package be.wegenenverkeer.mosaic.infrastructure

import be.wegenenverkeer.mosaic.domain.model.MosaicEvent
import io.funcqrs.Tag

class EventTagger extends akka.persistence.pg.event.EventTagger {

  override def tags(event: Any): Map[String, String] = {
    def toMap(tags: Set[Tag]) =
      tags.map { tag =>
        (tag.key, tag.value)
      }.toMap

    event match {
      case evt: MosaicEvent => toMap(evt.tags)
      case _                => Map.empty
    }
  }
}