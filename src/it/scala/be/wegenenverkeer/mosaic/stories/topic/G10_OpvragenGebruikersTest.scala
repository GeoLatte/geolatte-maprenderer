package be.wegenenverkeer.mosaic.stories.topic

import be.wegenenverkeer.mosaic.dsl.ApplicationTestDsl
import be.wegenenverkeer.mosaic.test.{EmbeddedApp, StelAlsDan}
import org.scalatest.FeatureSpec

/**
 * Created by ingridspronk on 14/08/15.
 */
class G10_OpvragenGebruikersTest extends FeatureSpec with StelAlsDan with EmbeddedApp with ApplicationTestDsl {

  feature("Een gebruiker kan medewerker opzoeken en basisgegevens bekijken") {

    scenario("de webservice geeft enkel de basisgegevens van de medewerker als antwoord terug") {

      Stel("de databank bevat een medewerker met VoID = 0010 ")

      Als("input parameter 'VoID' = array [0010]")

      Dan("wordt 1 medewerker als antwoord gegevens ")
      En ("worden 'VoID', 'VlimpersID', 'naam', 'voornaam', 'functie', 'organisatie', 'actief' en 'bron' in het antwoord teruggegeven")
      En ("wordt return code 'succesvol' als antwoord gegeven")

      pending
    }
    scenario("geen input parameters is alles opzoeken (eerste 50 resultaten) ") {

      Stel("de databank bevat minstens 60 medewerkers")

      Als("er bij het opvragen van medewerkers geen enkele input parameter ingevuld is")

      Dan("worden de eerste 50 medewerkers als antwoord gegeven")
      En ("zijn deze alfabetisch gesorteerd op 'naam' ")
      En ("wordt return code 'succesvol' als antwoord gegeven")

      pending
    }

  }
}
