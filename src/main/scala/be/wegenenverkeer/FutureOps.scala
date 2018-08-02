package be.wegenenverkeer

import be.wegenenverkeer.restfailure.RestException

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

object FutureOps {

  implicit class MapFailure[+A](val future: Future[A]) extends AnyVal {

    /**
     * Map een failed Future naar failed Future met bekende RestExceptions
     */
    def mapFailure(pf: PartialFunction[Throwable, RestException])(implicit ex: ExecutionContext): Future[A] = {
      future.recoverWith {
        case e if pf.isDefinedAt(e) => Future.failed(pf(e))
        case NonFatal(e)            => Future.failed(RestException.internalError(e.getMessage))
      }
    }
  }
}
