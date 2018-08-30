package be.wegenenverkeer.uuid

import java.nio.charset.Charset
import java.util.UUID


/**
 * Een class die een UUID kan genereren adhv een initial seed.
 * Als je [[next]] called krijg je een UUIDGen die al de volgende UUID bevat.
 * De volgende UUID is afgeleid van de huidige, dus deze zal altijd dezelfde zijn.
 * Op deze manier kun je dus een stabiele,repeatable reeks van UUIDs genereren.
 * 
 * Dit is bvb heel interessant bij een atomfeed, waarbij we voor 1 domain event meerdere feedentries willen maken.
 * Dan is de seed value de uuid van het event, en de uuid van de feedentries zullen daarvan afgeleid worden.
 * 
 * @param seed de seed waarmee de initiele UUID berekend zal worden.
 */
class UUIDGen private(seed: String) {

  val uuid = UUID.nameUUIDFromBytes(seed.getBytes(UUIDGen.utf8))

  def next: UUIDGen = new UUIDGen(uuid.toString)
}

object UUIDGen {

  private val utf8 = Charset.forName("UTF-8")

  def apply(seedUUID: UUID): UUIDGen = {
    new UUIDGen(seedUUID.toString)
  }

  def apply(seed:String): UUIDGen = {
    new UUIDGen(seed)
  }

}