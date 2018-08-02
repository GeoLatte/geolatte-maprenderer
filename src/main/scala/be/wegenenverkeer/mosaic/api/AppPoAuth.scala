package be.wegenenverkeer.mosaic.api

import be.wegenenverkeer.mosaic.AppMachtigingen
import be.wegenenverkeer.poauth.api.PlayPoAuth
import be.wegenenverkeer.poauth.domain.model.GebruikerMachtigingen
import be.wegenenverkeer.poauth.infrastructure.PoProvider
import be.wegenenverkeer.restfailure.RestException
import be.wegenenverkeer.restfailure.RestFailure.Forbidden
import play.api.Logger
import play.api.mvc.Request

import scala.concurrent.{ExecutionContext, Future}

class AppPoAuth(poProvider: PoProvider, cacheEnabled: Boolean = true) {

  val logger = Logger(classOf[AppPoAuth])

  val playPoAuth = new PlayPoAuth(poProvider = poProvider, cacheEnabled)

  def indienGekendeGebruiker[T](request: Request[_])(body: GebruikerMachtigingen => Future[T])(implicit ec: ExecutionContext): Future[T] = {
    authorizedInternal(request, (gbm: GebruikerMachtigingen) => true)(body)
  }

  def indienAdmin[T](request: Request[_])(body: GebruikerMachtigingen => Future[T])(implicit ec: ExecutionContext): Future[T] = {
    val check = (gbm: GebruikerMachtigingen) =>
      gbm.machtigingen.exists(m => m.rol.code == AppMachtigingen.adminRol && m.toepassing == AppMachtigingen.toepassingCode)

    authorizedInternal(request, check)(body)
  }

  private def authorizedInternal[T](request: Request[_], autorisatieControle: GebruikerMachtigingen => Boolean)(body: GebruikerMachtigingen => Future[T])(implicit ec: ExecutionContext): Future[T] = {

    val f: GebruikerMachtigingen => Future[GebruikerMachtigingen] =
      (gebruikerMachtigingen) =>
        if (autorisatieControle(gebruikerMachtigingen)) {
          Future.successful(gebruikerMachtigingen)
        } else {
          val msg = s"De gebruiker ${
            gebruikerMachtigingen.gebruiker.voId
          } heeft niet de nodige machtigingen voor deze actie om aan ${request.method} ${request.path} te geraken."

          logger.debug(s"Machtigingen waren: ${gebruikerMachtigingen.machtigingen.mkString(",")}")
          logger.info(msg)

          Future.failed(RestException(Forbidden(msg)))
        }

    playPoAuth.authorized(request, f)(body)
  }

}
