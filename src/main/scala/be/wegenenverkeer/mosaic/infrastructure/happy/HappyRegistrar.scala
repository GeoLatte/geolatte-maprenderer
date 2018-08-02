package be.wegenenverkeer.mosaic.infrastructure.happy

import java.io.File
import java.time.LocalDateTime

import akka.actor.ActorSystem
import akka.util.Timeout
import be.wegenenverkeer.appstatus.info.providers.{ GitInfoProvider, JvmInfoProvider }
import be.wegenenverkeer.appstatus.info.{ InfoDetails, InfoRegistry }
import be.wegenenverkeer.appstatus.status.components.JdbcComponent
import be.wegenenverkeer.appstatus.status.{ ComponentInfo, ComponentRegistry }
import be.wegenenverkeer.appstatus.rood.{ FeedPointersComponent, JournalComponent, OffsetsComponent }
import be.wegenenverkeer.appstatus.support.PlaySupport._
import be.wegenenverkeer.mosaic.BuildInfo
import play.api.{ Application, Configuration }
import slick.jdbc.JdbcBackend.DatabaseDef

import scala.concurrent.duration._

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

    componentRegistry.register(OffsetsComponent.defaultInfo, new OffsetsComponent(()           => db.createSession().conn))
    componentRegistry.register(JournalComponent.defaultInfo, new JournalComponent(()           => db.createSession().conn))
    componentRegistry.register(FeedPointersComponent.defaultInfo, new FeedPointersComponent(() => db.createSession().conn))

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
