package be.wegenenverkeer.mosaic.infrastructure.slick

object CustomOperators {

  import be.wegenenverkeer.mosaic.infrastructure.SlickPgProfile.api._

  private val opIlike = SimpleBinaryOperator.apply[Boolean]("ILIKE")
  def iLikeString(left: Rep[String], right: String) = opIlike(left, right)
  def iLikeStringOption(left: Rep[Option[String]], right: String) = opIlike(left, right)

}
