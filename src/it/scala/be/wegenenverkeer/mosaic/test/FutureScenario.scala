package be.wegenenverkeer.mosaic.test

import be.wegenenverkeer.mosaic.BuildInfo
import org.scalatest.FeatureSpec

trait FutureScenario extends FeatureSpec {

  /**
   * Voert een ganse Feature pas uit als de huidige versie van de applicatie gelijk of later is dan de vanaf versie
   */
  def futureFeature(vanaf: String)(message: String)(f: => Unit) = {
    execute(vanaf)(feature(message)(f))
  }

  /**
   * Voert een scenario pas uit als de huidige versie van de applicatie gelijk of later is dan de vanaf versie
   */
  def futureScenario(vanaf: String)(message: String)(f: => Unit) = {
    execute(vanaf)(scenario(message)(f))
  }

  def futurePending(vanaf: String) = {
    execute(vanaf)(pending)
  }

  /**
   * Voert een scenario pas uit als de huidige versie van de applicatie gelijk of later is dan de vanaf versie
   */
  private def execute(vanaf: String)(f: => Unit) = {
    val huidigeVersie = parseVersie(BuildInfo.version)
    val vanafVersie = parseVersie(vanaf)

    if (moetActiefWorden(huidigeVersie, vanafVersie)) {
      f
    } else {
      //NOOP, de hudige versie is nog kleiner dan de vanaf versie
    }

  }

  /**
   * Parsed de versie in zijn components, negeert - (dash) en alles erna , dropped insignificant trailing zeros
   */
  private def parseVersie(versie: String): List[Int] = {

    // neem alles voor de -
    val mainVersie = versie.takeWhile(_ != '-')

    // splits op de . drop de trailing zeros
    mainVersie.split('.').map(_.toInt).toList.reverse.dropWhile(_ == 0).reverse
  }

  // het scenario moet uitgevoerd worden vanaf dat de huidige versie gelijk of later is dan de vanafVersie
  private def moetActiefWorden(currentVersieArg: List[Int], vanafVersieArg: List[Int]): Boolean = {

    //zorg dat versies zelfde lengte hebben
    val maxLenght = currentVersieArg.length.max(vanafVersieArg.length)
    val currentVersie = currentVersieArg ++ List.fill(maxLenght - currentVersieArg.length)(0)
    val vanafVersie = vanafVersieArg ++ List.fill(maxLenght - vanafVersieArg.length)(0)

    val zipped = currentVersie.zip(vanafVersie)

    zipped.foldLeft[Option[Boolean]](None) {
      case (moetActiefWorden, (current, vanaf)) =>
        moetActiefWorden match {
          case Some(activeren)         => Some(activeren)
          case None if current > vanaf => Some(true)
          case None if current < vanaf => Some(false)
          case None                    => None //voorlopig gelijk
        }
    }.getOrElse(true) //indien gelijk, moet het actief worden
  }

}
