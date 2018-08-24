package be.wegenenverkeer.mosaic.infrastructure.happy

import java.io.File
import java.time.LocalDateTime

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import be.wegenenverkeer.appstatus.info.providers.{GitInfoProvider, JvmInfoProvider}
import be.wegenenverkeer.appstatus.info.{InfoDetails, InfoRegistry}
import be.wegenenverkeer.appstatus.rood.FeedPointersComponent
import be.wegenenverkeer.appstatus.status
import be.wegenenverkeer.appstatus.status.ComponentStatus.{OkStatus, WarningStatus}
import be.wegenenverkeer.appstatus.status.components.JdbcComponent
import be.wegenenverkeer.appstatus.status.{Component, ComponentInfo, ComponentRegistry, ComponentValue}
import be.wegenenverkeer.appstatus.support.PlaySupport._
import be.wegenenverkeer.mosaic.BuildInfo
import be.wegenenverkeer.mosaic.domain.service.geowebcache.GWCInvalidatorActor
import be.wegenenverkeer.mosaic.domain.service.geowebcache.GWCInvalidatorActor.InvalidatorActorStatus
import be.wegenenverkeer.mosaic.domain.service.storage.EnvelopeStorage
import be.wegenenverkeer.mosaic.domain.service.{DataloaderService, VerkeersbordenService}
import com.softwaremill.tagging.@@
import play.api.libs.json.Json
import play.api.{Application, Configuration}
import slick.jdbc.JdbcBackend.DatabaseDef

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class HappyRegistrar(
    dbOpt: Option[DatabaseDef],
    actorSystem: ActorSystem,
    configuration: Configuration,
    application: Application,
    infoRegistry: InfoRegistry,
    componentRegistry: ComponentRegistry,
    dataloaderServiceOpt: Option[DataloaderService],
    verkeersbordenServiceOpt: Option[VerkeersbordenService],
    envelopeStorage: EnvelopeStorage,
    gwcInvalidatorActorSupervisor: ActorRef @@ GWCInvalidatorActor
) {

  val timeout: FiniteDuration   = 5.seconds
  implicit val askTimeout       = Timeout(timeout)
  implicit val executionContext = actorSystem.dispatcher

  def register() = {

    registerStatus()
    registerInfo()
  }

  def registerStatus() = {

    dbOpt.foreach { db =>
      componentRegistry.register(ComponentInfo("db", "Database", ""), new JdbcComponent(() => db.createSession().conn))
    }

    dbOpt.foreach { db =>
      componentRegistry.register(FeedPointersComponent.defaultInfo, new FeedPointersComponent(() => db.createSession().conn))
    }

    dataloaderServiceOpt.foreach { dataloaderService =>
      componentRegistry.register(
        ComponentInfo("dataloader", "Dataloader feed positie", ""),
        new Component {
          override def check(implicit excCtx: ExecutionContext): Future[ComponentValue] = {
            dataloaderService.getVerkeersbordenFeedPosition("/rest/events/zi/verkeersborden/feed").map { r =>
              status.ComponentValue(
                status = OkStatus,
                value  = r.toString
              )
            }
          }
        }
      )
    }

    verkeersbordenServiceOpt.foreach { verkeersbordenService =>
      componentRegistry.register(
        ComponentInfo("verkeersborden", "Verkeersborden feed", ""),
        new Component {
          override def check(implicit excCtx: ExecutionContext): Future[ComponentValue] = {
            verkeersbordenService.getFeedPage("").map { body =>
              status.ComponentValue(
                status = OkStatus,
                value  = body.take(100) + Some("...").filter(_ => body.length > 100).getOrElse("")
              )
            }
          }
        }
      )
    }

    componentRegistry.register(
      ComponentInfo("envelopes", "Aantal envelopes te verwerken", ""),
      new Component {
        override def check(implicit excCtx: ExecutionContext): Future[ComponentValue] = {
          envelopeStorage.aantalItemsBeschikbaar().map { aantal =>
            status.ComponentValue(
              status = OkStatus,
              value  = aantal.toString
            )
          }
        }
      }
    )

    componentRegistry.register(
      ComponentInfo("GWCInvalidatorActorStatus", "GWCInvalidatorActor status", ""),
      new Component {
        override def check(implicit excCtx: ExecutionContext): Future[ComponentValue] = {
          gwcInvalidatorActorSupervisor.ask(GWCInvalidatorActor.GetStatus).map { s =>
            val actorStatus: InvalidatorActorStatus = s.asInstanceOf[InvalidatorActorStatus]
            status.ComponentValue(
              status = if (actorStatus.okStatus) { OkStatus } else { WarningStatus("") },
              value  = Json.prettyPrint(Json.toJson(actorStatus)(Json.format[InvalidatorActorStatus]))
            )
          }
        }
      }
    )

  }

  def registerInfo() = {

    //JVM
    infoRegistry.register(new JvmInfoProvider())

    //GIT
    // sbt-git-stamp plugin niet compatibel ..
    // infoRegistry.register(GitInfoProvider(HappyRegistrar.getClass))

    //Play
    infoRegistry.registerInfoFromPlay(() => application)

    //Opstarttijd
    infoRegistry.register(InfoDetails("start", "Opstarttijd", ""), LocalDateTime.now().toString)

    infoRegistry.register(InfoDetails("version", "versie", "Versie"), BuildInfo.version)

    infoRegistry.registerDynamic(InfoDetails("heapdump", "Heap dump genomen", ""), () => {
      new File("/tmp/heap.hprof").lastModified().toString
    })

    def registerConfig(path: String): Unit = {
      infoRegistry.register(
        InfoDetails(s"config.$path", s"Config: $path", ""),
        configuration.getOptional[String](path).getOrElse("Niet geconfigureerd")
      )
    }

    registerConfig("dataloader.url")
    registerConfig("verkeersborden.url")
    registerConfig("geowebcache.url")
    registerConfig("geowebcache.layer")
    registerConfig("aws.s3.files.bucket.name")
    registerConfig("aws.s3.files.bucket.object-prefix.reader")
    registerConfig("aws.s3.files.bucket.object-prefix.writers")

  }

  register()

}

object HappyRegistrar {}
