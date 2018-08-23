package be.wegenenverkeer.mosaic.infrastructure.apploader

import be.wegenenverkeer.mosaic.infrastructure.HappyNoDbDepsModule
import play.api.ApplicationLoader.Context
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext}

import scala.concurrent.Future
import scala.util.Try

class PubAppLoader extends ApplicationLoader {

  def load(context: Context): Application = {

    val app = new BuiltInComponentsFromContext(context) with AppComponents with HappyNoDbDepsModule

    app.applicationLifecycle.addStopHook { () =>
      import scala.concurrent.ExecutionContext.Implicits.global
      for {
        _ <- app.actorSystem.terminate()
        _ <- app.actorSystem.whenTerminated
        _ <- Future.fromTry(Try(app.poProvider.close()))
      } yield ()
    }
    app.application
  }

}
