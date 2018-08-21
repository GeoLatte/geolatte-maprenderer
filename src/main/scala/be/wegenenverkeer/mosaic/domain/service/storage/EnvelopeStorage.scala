package be.wegenenverkeer.mosaic.domain.service.storage

import be.wegenenverkeer.mosaic.domain.model.CRS
import org.geolatte.geom.{C2D, Envelope}
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.util.Try

trait EnvelopeStorage {

  def envelopeToString(envelope: Envelope[C2D]): String = {
    val min       = envelope.lowerLeft()
    val max       = envelope.upperRight()
    Json.stringify(Json.toJson(Seq(min.getX, min.getY, max.getX, max.getY)))
  }

  def stringToEnvelope(content: String): Try[Envelope[C2D]] = Try {
    val coords = Json.parse(content).validate[Seq[Double]].getOrElse(throw new Exception("Could not parse envelope"))

    val minX :: minY :: maxX :: maxY :: Nil = coords

    new Envelope[C2D](minX, minY, maxX, maxY, CRS.LAMBERT72)
  }

  def schrijf(envelope: Envelope[C2D]): Future[Unit]

  def lees(limit: Int, uitgezonderd: Set[String]): Future[List[EnvelopeFile]]

  def verwijder(fileRef: String): Future[Unit]

  def aantalItemsBeschikbaar(): Future[Long]

}

case class EnvelopeFile(envelope: Envelope[C2D], fileRef: String)
