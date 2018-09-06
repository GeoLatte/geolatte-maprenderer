package be.wegenenverkeer.mosaic.infrastructure.apploader

import be.wegenenverkeer.mosaic.infrastructure.{AppAtomium, AppDbModule, HappyDbDepsModule}
import be.wegenenverkeer.playevolutions.MacWireEvolutionsModule
import play.api.ApplicationLoader.Context
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext}

import scala.concurrent.Future
import scala.util.Try

class FileSystemAppLoader extends ApplicationLoader {

  def load(context: Context): Application = {

    val app = new BuiltInComponentsFromContext(context)
      with MacWireEvolutionsModule
      with AppComponents
      with DbModule
      with AppDbModule
      with FileSystemEnvelopeStorageModule
      with AppAtomium
      with HappyDbDepsModule

    app.applicationLifecycle.addStopHook { () =>
      import scala.concurrent.ExecutionContext.Implicits.global
      for {
        _ <- app.actorSystem.terminate()
        _ <- app.actorSystem.whenTerminated
        _ <- Future.fromTry(Try(app.verkeersbordenFeedConsumer.stop()))
        _ <- Future.fromTry(Try(app.db.close()))
        _ <- Future.fromTry(Try(app.poProvider.close()))
      } yield ()
    }
    app.application
  }

}
