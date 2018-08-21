package be.wegenenverkeer.mosaic.domain.service.storage

import org.geolatte.geom.{C2D, Envelope}

import scala.concurrent.{ExecutionContext, Future}

class MultipleWritersEnvelopeStorage(reader: EnvelopeStorage, writers: Seq[EnvelopeStorage])(implicit exc: ExecutionContext)
    extends EnvelopeStorage {

  override def schrijf(envelope: Envelope[C2D]): Future[Unit] = {

    val futures = writers.map(writer => writer.schrijf(envelope)) // parallel

    Future.sequence(futures).map(_ => Unit)
  }

  override def lees(limit: Int, uitgezonderd: Set[String]): Future[List[EnvelopeFile]] = reader.lees(limit, uitgezonderd)

  override def verwijder(fileRef: String): Future[Unit] = reader.verwijder(fileRef)

  override def aantalItemsBeschikbaar(): Future[Long] = reader.aantalItemsBeschikbaar()

}
