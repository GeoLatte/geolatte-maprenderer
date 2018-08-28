package be.wegenenverkeer.mosaic.domain.service.geowebcache

import java.time.temporal.ChronoUnit
import java.time.{LocalDateTime, ZoneId}

import akka.actor.{Actor, Props}
import be.wegenenverkeer.mosaic.domain.service.geowebcache.GWCSeedActor._
import be.wegenenverkeer.mosaic.util.Logging

import scala.concurrent.duration._
import scala.util.{Failure, Success}

class GWCSeedActor(geowebcacheService: GeowebcacheService) extends Actor with Logging {

  import context.dispatcher

  var laatsteSeedMoment: Option[LocalDateTime] = None
  var error: Option[String] = None

  context.system.scheduler.schedule(5.minutes, 5.minutes, self, SeedIndienNodig)

  override def receive: Receive = {
    case SeedIndienNodig =>
      if (buitenKantoorUren(nu) && isLangGenoegGeleden(laatsteSeedMoment)) {
        geowebcacheService.seed().onComplete {
          case Success(_) =>
            logger.info("Seeding gestart.")
            error = None
          case Failure(t) =>
            logger.error("Seeding mislukt!", t)
            error = Some(t.getMessage)
        }
        laatsteSeedMoment = Some(nu)
      }

    case GetStatus =>
      sender ! Status(laatsteSeedMoment.toString, error)
  }

}

object GWCSeedActor {

  case object SeedIndienNodig
  case object GetStatus

  case class Status(laatsteSeedMoment: String, error: Option[String]) {
    def okStatus: Boolean = error.isEmpty
  }

  val zoneId: ZoneId = ZoneId.of("Europe/Brussels")

  def nu: LocalDateTime = LocalDateTime.now(zoneId)

  def buitenKantoorUren(dateTime: LocalDateTime): Boolean = {
    val hetUurNu = dateTime.getHour + 1
    hetUurNu >= 20 || hetUurNu < 6
  }

  def isLangGenoegGeleden(laatsteSeedMoment: Option[LocalDateTime]): Boolean = {
    laatsteSeedMoment match {
      case None             => true
      case Some(seedMoment) => seedMoment.until(nu, ChronoUnit.HOURS) >= 18
    }
  }

  def props(geowebcacheService: GeowebcacheService) = Props(new GWCSeedActor(geowebcacheService))

}
