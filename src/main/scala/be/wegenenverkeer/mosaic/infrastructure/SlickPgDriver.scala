package be.wegenenverkeer.mosaic.infrastructure

import com.github.tminglei.slickpg._
import play.api.libs.json.{Json, JsValue}

trait SlickPgProfile
    extends ExPostgresProfile with PgDate2Support with PgPlayJsonSupport with PgArraySupport
    with array.PgArrayJdbcTypes {

  override val pgjson = "jsonb"

  override val api = MyAPI

  object MyAPI extends API with DateTimeImplicits with JsonImplicits with ArrayImplicits with SimpleArrayPlainImplicits {

    // De volgende mappgins zijn nodig om te kunnen query op json.
    // Er is geen documentatie in slick-pg project.
    // Ik heb dat in een test suite gevonden en ik heb ook geen idee waarom moet dat zo zijn.
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)
    implicit val jsValueJsonArrayTypeMapper =
      new AdvancedArrayJdbcType[JsValue](
        pgjson,
        (s) => utils.SimpleArrayUtils.fromString[JsValue](Json.parse)(s).orNull,
        (v) => utils.SimpleArrayUtils.mkString[JsValue](_.toString())(v)
      ).to(_.toList)
  }

}

object SlickPgProfile extends SlickPgProfile
