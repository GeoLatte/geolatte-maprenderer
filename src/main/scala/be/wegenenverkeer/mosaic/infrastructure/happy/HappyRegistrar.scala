package be.wegenenverkeer.mosaic.infrastructure.happy

import java.io.File
import java.net.URL
import java.time.LocalDateTime

import akka.actor.ActorSystem
import akka.util.Timeout
import be.wegenenverkeer.api.dataloader.DataloaderApi
import be.wegenenverkeer.appstatus.info.providers.{ GitInfoProvider, JvmInfoProvider }
import be.wegenenverkeer.appstatus.info.{ InfoDetails, InfoRegistry }
import be.wegenenverkeer.appstatus.rood.FeedPointersComponent
import be.wegenenverkeer.appstatus.status
import be.wegenenverkeer.appstatus.status.ComponentStatus.OkStatus
import be.wegenenverkeer.appstatus.status.components.JdbcComponent
import be.wegenenverkeer.appstatus.status.{ Component, ComponentInfo, ComponentRegistry, ComponentValue }
import be.wegenenverkeer.appstatus.support.PlaySupport._
import be.wegenenverkeer.mosaic.BuildInfo
import play.api.libs.json.Json
import play.api.{ Application, Configuration }
import slick.jdbc.JdbcBackend.DatabaseDef

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }

class HappyRegistrar(
    db: DatabaseDef,
    actorSystem: ActorSystem,
    configuration: Configuration,
    application: Application,
    infoRegistry: InfoRegistry,
    componentRegistry: ComponentRegistry
) {

  val timeout: FiniteDuration   = 5.seconds
  implicit val askTimeout       = Timeout(timeout)
  implicit val executionContext = actorSystem.dispatcher

  def register() = {

    registerStatus()
    registerInfo()
  }

  def registerStatus() = {

    componentRegistry.register(ComponentInfo("db", "Database", ""), new JdbcComponent(() => db.createSession().conn))

    componentRegistry.register(FeedPointersComponent.defaultInfo, new FeedPointersComponent(() => db.createSession().conn))

    componentRegistry.register(
      ComponentInfo("dataloader", "Dataloader status", ""),
      new Component {
        override def check(implicit excCtx: ExecutionContext): Future[ComponentValue] = {
          val url = configuration.get[String]("dataloader.url")
          DataloaderApi(new URL(url)).job.jobNaam("verkeersborden").get().map { r =>
            status.ComponentValue(
              status = OkStatus,
              value  = r.jsonBody.map(Json.prettyPrint).getOrElse(s"$url: ${r.status}: ${r.stringBody.map(_.take(200)).getOrElse("")}")
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
    infoRegistry.register(GitInfoProvider(HappyRegistrar.getClass))

    //Play
    infoRegistry.registerInfoFromPlay(() => application)

    //Opstarttijd
    infoRegistry.register(InfoDetails("start", "Opstarttijd", ""), LocalDateTime.now().toString)

    infoRegistry.register(InfoDetails("version", "versie", "Versie"), BuildInfo.version)

    infoRegistry.registerDynamic(InfoDetails("heapdump", "Heap dump genomen", ""), () => {
      new File("/tmp/heap.hprof").lastModified().toString
    })

  }

  register()

}

object HappyRegistrar {}
