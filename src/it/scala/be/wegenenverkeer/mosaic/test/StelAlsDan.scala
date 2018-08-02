package be.wegenenverkeer.mosaic.test

import org.scalatest.{GivenWhenThen, Informing}

trait StelAlsDan extends GivenWhenThen {
  this: Informing =>

  def Stel(message: String) = {
    Given(message)
  }

  def Als(message: String) = {
    When(message)
  }

  def Dan(message: String) = {
    Then(message)
  }

  def En(message: String) = {
    And(message)
  }

}
