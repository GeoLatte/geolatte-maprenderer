package be.wegenenverkeer.mosaic.test

import java.time.temporal.ChronoUnit

import be.wegenenverkeer.mosaic.domain.service.geowebcache.GWCSeedActor._
import org.scalatest.FunSuite

class GWCSeedActorTest extends FunSuite {

  test("Lang Genoeg geleden") {

    assert(isLangGenoegGeleden(None) === true)

    val langGenoeg = nu.minus(23, ChronoUnit.HOURS)
    assert(isLangGenoegGeleden(Some(langGenoeg)) === true)

    val nietLangGenoeg = nu.minus(2, ChronoUnit.HOURS)
    assert(isLangGenoegGeleden(Some(nietLangGenoeg)) === false)
  }

  test("Buiten kantooruren") {
    val buiten = nu.withHour(2)
    assert(buitenKantoorUren(buiten) === true)

    val nietBuiten = nu.withHour(13)
    assert(buitenKantoorUren(nietBuiten) === false)

  }

}
