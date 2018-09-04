package be.wegenenverkeer.mosaic.domain.service.geowebcache

import akka.actor.{Actor, PoisonPill, Props, Status}
import akka.pattern.pipe
import be.wegenenverkeer.mosaic.domain.service.geowebcache.GWCInvalidatorActor._
import be.wegenenverkeer.mosaic.domain.service.storage.{EnvelopeFile, EnvelopeReader}
import be.wegenenverkeer.mosaic.util.Logging

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class GWCInvalidatorActor(envelopeStorage: EnvelopeReader, geowebcacheService: GeowebcacheService) extends Actor with Logging {

  import context.dispatcher

  val service = GWCInvalidatorActorServices(envelopeStorage, geowebcacheService)

  val envelopesTeInvalideren: mutable.Queue[EnvelopeFile] = mutable.Queue[EnvelopeFile]()
  val envelopesTeVerwijderen: mutable.Queue[EnvelopeFile] = mutable.Queue[EnvelopeFile]()
  val fileIdsInVerwerking: mutable.Set[String]           = mutable.Set[String]()

  // start lees 'loop'
  context.system.scheduler.scheduleOnce(0.seconds, self, LeesIndienNodig)

  // twee invalideer 'loops'
  context.system.scheduler.scheduleOnce(1.seconds, self, Invalideer)
  context.system.scheduler.scheduleOnce(3.seconds, self, Invalideer)

  // twee verwijder 'loops'
  context.system.scheduler.scheduleOnce(5.seconds, self, Verwijder)
  context.system.scheduler.scheduleOnce(7.seconds, self, Verwijder)

  override def receive: Receive = {
    case GetStatus =>
      sender ! InvalidatorActorStatus(
        envelopesTeInvalideren = envelopesTeInvalideren.size,
        envelopesTeVerwijderen = envelopesTeVerwijderen.size,
        fileRefsInVerwerking   = fileIdsInVerwerking.size
      )

    case LeesIndienNodig =>
      if (envelopesTeInvalideren.size <= maxQueueSize) {
        if (envelopesTeVerwijderen.size <= maxQueueSize) {
          self ! Lees
        } else {
          logger.warn(s"Niet lezen, want verwijderen kan niet volgen (${envelopesTeVerwijderen.size})")
          context.system.scheduler.scheduleOnce(leesIntervalBackPressure, self, LeesIndienNodig)
        }
      } else {
        logger.warn(s"Niet lezen, want invalideren kan niet volgen (${envelopesTeInvalideren.size})")
        context.system.scheduler.scheduleOnce(leesIntervalBackPressure, self, LeesIndienNodig)
      }

    case Lees =>
      service.lees(limit = 100, uitgezonderd = fileIdsInVerwerking.toSet).pipeTo(self)

    case Gelezen(envelopes: List[EnvelopeFile]) =>
      if (envelopes.nonEmpty) {
        logger.debug(s"${envelopes.size} envelope file(s) gelezen")

        fileIdsInVerwerking ++= envelopes.map(_.fileId)
        self ! LeesIndienNodig

        envelopesTeInvalideren.enqueue(envelopes: _*) // deze zullen opgepikt worden door een invalideer 'loop'
      } else {
        context.system.scheduler.scheduleOnce(leesInterval, self, LeesIndienNodig)
      }

    case Invalideer =>
      if (envelopesTeInvalideren.nonEmpty) {
        val envelopeFile = envelopesTeInvalideren.dequeue()
        logger.debug(s"Invalideren $envelopeFile")
        service.invalideer(envelopeFile).pipeTo(self)
      } else {
        // check opnieuw binnen 5 sec
        context.system.scheduler.scheduleOnce(5.seconds, self, Invalideer)
      }

    case GeInvalideerd(envelopFile) =>
      self ! Invalideer // invalideer de volgende
      envelopesTeVerwijderen.enqueue(envelopFile) // deze zullen opgepikt worden door een verwijder 'loop'

    case Verwijder =>
      if (envelopesTeVerwijderen.nonEmpty) {
        val envelopFile = envelopesTeVerwijderen.dequeue()
        logger.debug(s"Verwijderen file: ${envelopFile.fileId}")
        service.verwijder(envelopFile).pipeTo(self)
      } else {
        // check opnieuw binnen 5 sec
        context.system.scheduler.scheduleOnce(5.seconds, self, Verwijder)
      }

    case Verwijderd(envelopFile) =>
      fileIdsInVerwerking -= envelopFile.fileId
      self ! Verwijder // verwijder de volgende

    case Status.Failure(f) =>
      logger.warn("Een operatie is mislukt, we herstarten", f)
      self ! PoisonPill
  }

}

object GWCInvalidatorActor {

  val maxQueueSize                             = 1000
  val leesInterval: FiniteDuration             = 5.seconds
  val leesIntervalBackPressure: FiniteDuration = 30.seconds

  case object GetStatus
  case class InvalidatorActorStatus(envelopesTeInvalideren: Int, envelopesTeVerwijderen: Int, fileRefsInVerwerking: Int) {
    def okStatus: Boolean = envelopesTeInvalideren < maxQueueSize && envelopesTeVerwijderen < maxQueueSize
  }

  case object LeesIndienNodig
  case object Lees
  case object Invalideer
  case object Verwijder

  case class Gelezen(envelopFiles: List[EnvelopeFile])
  case class GeInvalideerd(envelopFile: EnvelopeFile)
  case class Verwijderd(envelopFile: EnvelopeFile)

  case class GWCInvalidatorActorServices(envelopeReader: EnvelopeReader, geowebcacheService: GeowebcacheService)(
      implicit exc: ExecutionContext) {

    def lees(limit: Int, uitgezonderd: Set[String]): Future[Gelezen] = {
      envelopeReader.lees(limit, uitgezonderd).map(Gelezen)
    }

    def invalideer(envelopeFile: EnvelopeFile): Future[GeInvalideerd] = {
      geowebcacheService.invalidate(envelopeFile.envelope).map(_ => GeInvalideerd(envelopeFile))
    }

    def verwijder(envelopeFile: EnvelopeFile): Future[Verwijderd] = {
      envelopeReader.verwijder(envelopeFile).map(_ => Verwijderd(envelopeFile))
    }
  }

  def props(envelopeReader: EnvelopeReader, geowebcacheService: GeowebcacheService) =
    Props(new GWCInvalidatorActor(envelopeReader, geowebcacheService))

}
