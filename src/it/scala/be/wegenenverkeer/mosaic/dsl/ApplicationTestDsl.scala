package be.wegenenverkeer.mosaic.dsl

import java.util.UUID

import be.wegenenverkeer.mosaic.test.IntegrationTest
import be.wegenenverkeer.poauth.api.PoAuth
import org.scalatest.Matchers
import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import play.api.libs.json.{JsObject, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.{postfixOps, reflectiveCalls}

trait ApplicationTestDsl extends Matchers with IntegrationTest with Eventually with IntegrationPatience {

  val localadmin = "localadmin"
  val localadminHeader = PoAuth.VOID_HEADER -> localadmin
  val nobodyHeader = PoAuth.VOID_HEADER -> "nobody"


}
