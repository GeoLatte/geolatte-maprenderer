package be.wegenenverkeer.mosaic.test

import be.wegenenverkeer.api.mosaic.dsl.scalaplay.Response
import be.wegenenverkeer.restfailure.{RestException, RestFailure}
import org.scalatest.exceptions.TestFailedException

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.control.NonFatal

trait IntegrationTest {

  implicit class FutureHttpOps[T](val future: Future[T]) {

    /**
     * Blokkeer op het result van een Future
     */
    def block(implicit exc: ExecutionContext): T = {
      blockFor(10.seconds)
    }

    /**
     * Blokkeer op het result van een Future voor gegeven duration
     */
    def blockFor(duration: Duration)(implicit exc: ExecutionContext): T = {
      Await.result(future, duration)
    }

    /**
     * Blokkeer op de Future en verwacht dat de Future faalt met een RestException
     *
     * @param onFailure krijgt de RestFailure die in de RestException zat
     */
    def expectRestFailure(onFailure: RestFailure => Unit)(implicit exc: ExecutionContext): Unit = {
      try {
        val res = block
        sys.error(s"De future moest falen maar was een success met als resultaat $res")
      } catch {
        case RestException(restfailure) => onFailure(restfailure)
        case NonFatal(e) =>
          e.printStackTrace()
          sys.error(s"We verwachten een RestException maar is gefaald met een $e")
      }
    }
  }

  implicit class ResponseOps[T](val response: Response[T]) {

    def isOk: T = expected(200)(response.body.get)

    def isAccepted: String = expectedStringBody(202)

    def isNoContent: String = expectedStringBody(204)

    def isUnauthorized: String = expectedStringBody(401)

    def isForbidden: String = expectedStringBody(403)

    def isPreconditionFailed: String = expectedStringBody(412)

    def isInternalError: String = expectedStringBody(500)

    /** Check expect code en return String body */
    private def expectedStringBody(code: Int): String = expected(code)(response.stringBody.getOrElse(""))

    private def expected[A](code: Int)(block: => A): A = {
      assert(response.status == code, s"Verwachtte status $code maar was ${response.status}. Body was ${response.stringBody}")
      block
    }

    /**
     * isOkOrFail is nuttig in combinatie met eventually(...) omdat eventually een exception verwacht zolang het antwoord er nog niet is.
     */
    def isOkOrFail: T = {
      if (response.status != 200) throw new TestFailedException("We ontvingen (nog) geen 200 antwoord.", 1)
      else response.body.get
    }

    def isAcceptedOrFail: String = {
      if (response.status != 202) throw new TestFailedException("We ontvingen (nog) geen 204 antwoord.", 1)
      else response.stringBody.getOrElse("")
    }

    /**
     * isNoContentOrFail is nuttig in combinatie met eventually(...) omdat eventually een exception verwacht zolang het antwoord er nog niet is.
     */
    def isNoContentOrFail: String = {
      if (response.status != 204) throw new TestFailedException("We ontvingen (nog) geen 204 antwoord.", 1)
      else response.stringBody.getOrElse("")
    }

  }

}

package object integrationTest extends IntegrationTest {

}
