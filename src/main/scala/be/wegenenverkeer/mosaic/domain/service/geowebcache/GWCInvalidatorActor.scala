package be.wegenenverkeer.mosaic.domain.service.geowebcache

import akka.actor.{Actor, PoisonPill, Props, Status}
import akka.pattern.pipe
import be.wegenenverkeer.mosaic.domain.service.geowebcache.GWCInvalidatorActor._
import be.wegenenverkeer.mosaic.domain.service.{EnvelopeFile, EnvelopeStorage}
import be.wegenenverkeer.mosaic.util.Logging

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class GWCInvalidatorActor(envelopeStorage: EnvelopeStorage) extends Actor with Logging {

  import context.dispatcher

  val service = GWCInvalidatorActorServices(envelopeStorage)

  val envelopesTeInvalideren: mutable.Queue[EnvelopeFile] = mutable.Queue[EnvelopeFile]()
  val envelopesTeVerwijderen: mutable.Queue[String]       = mutable.Queue[String]()
  val fileRefsInVerwerking: mutable.Set[String]           = mutable.Set[String]()

  val leesInterval: FiniteDuration = 5.seconds

  // start lees 'loop'
  context.system.scheduler.scheduleOnce(0.seconds, self, LeesIndienNodig)

  // twee invalideer 'loops'
  context.system.scheduler.scheduleOnce(1.seconds, self, Invalideer)
  context.system.scheduler.scheduleOnce(3.seconds, self, Invalideer)

  // twee verwijder 'loops'
  context.system.scheduler.scheduleOnce(5.seconds, self, Verwijder)
  context.system.scheduler.scheduleOnce(7.seconds, self, Verwijder)

  override def receive: Receive = {
    case LeesIndienNodig =>
      if (envelopesTeInvalideren.size <= 1000) {
        if (envelopesTeVerwijderen.size <= 1000) {
          self ! Lees
        } else {
          logger.debug(s"Niet lezen, want verwijderen kan niet volgen (${envelopesTeVerwijderen.size})")
          context.system.scheduler.scheduleOnce(leesInterval, self, LeesIndienNodig)
        }
      } else {
        logger.debug(s"Niet lezen, want invalideren kan niet volgen (${envelopesTeInvalideren.size})")
        context.system.scheduler.scheduleOnce(leesInterval, self, LeesIndienNodig)
      }

    case Lees =>
      service.lees(uitgezonderd = fileRefsInVerwerking.toSet).pipeTo(self)

    case Gelezen(envelopes: List[EnvelopeFile]) =>
      if (envelopes.nonEmpty) {
        logger.debug(s"${envelopes.size} envelope files gelezen")

        fileRefsInVerwerking ++= envelopes.map(_.fileRef)
        self ! LeesIndienNodig

        envelopesTeInvalideren.enqueue(envelopes: _*) // deze zullen opgepikt worden door een invalideer 'loop'
      } else {
        context.system.scheduler.scheduleOnce(leesInterval, self, LeesIndienNodig)
      }

    case Invalideer =>
      if (envelopesTeInvalideren.nonEmpty) {
        service.invalideer(envelopesTeInvalideren.dequeue()).pipeTo(self)
      } else {
        // check opnieuw binnen 5 sec
        context.system.scheduler.scheduleOnce(5.seconds, self, Invalideer)
      }

    case GeInvalideerd(fileRef) =>
      self ! Invalideer // invalideer de volgende
      envelopesTeVerwijderen.enqueue(fileRef) // deze zullen opgepikt worden door een verwijder 'loop'

    case Verwijder =>
      if (envelopesTeVerwijderen.nonEmpty) {
        service.verwijder(envelopesTeVerwijderen.dequeue()).pipeTo(self)
      } else {
        // check opnieuw binnen 5 sec
        context.system.scheduler.scheduleOnce(5.seconds, self, Verwijder)
      }

    case Verwijderd(fileRef) =>
      fileRefsInVerwerking -= fileRef
      self ! Verwijder // verwijder de volgende

    case Status.Failure(f) =>
      logger.warn("Een operatie is mislukt, we herstarten", f)
      self ! PoisonPill
  }

}

object GWCInvalidatorActor {

  case object LeesIndienNodig
  case object Lees
  case object Invalideer
  case object Verwijder

  case class Gelezen(envelopFiles: List[EnvelopeFile])
  case class GeInvalideerd(fileRef: String)
  case class Verwijderd(fileRef: String)

  case class GWCInvalidatorActorServices(envelopeStorage: EnvelopeStorage)(implicit exc: ExecutionContext) {
    def lees(uitgezonderd: Set[String]): Future[Gelezen] = {
      envelopeStorage.lees(limit = 100, uitgezonderd).map(Gelezen)
    }

    def invalideer(envelope: EnvelopeFile): Future[GeInvalideerd] = {
      Future {
        GeInvalideerd(envelope.fileRef)
      }
    }

    def verwijder(fileRef: String): Future[Verwijderd] = {
      envelopeStorage.verwijder(fileRef).map(_ => Verwijderd(fileRef))
    }
  }

  def props(envelopeStorage: EnvelopeStorage) =
    Props(new GWCInvalidatorActor(envelopeStorage))

}
