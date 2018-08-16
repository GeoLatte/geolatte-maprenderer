package be.wegenenverkeer.mosaic.infrastructure
import be.wegenenverkeer.atomium.extension.feedconsumer.{ FeedPosition, FeedPositionRepo }
import be.wegenenverkeer.slick3.DbRunner

import scala.concurrent.ExecutionContext
import SlickPgProfile.api._

class StartInitieelOpCustomPositieFeedPositionRepo(customPositieDbioProvider: () => DBIO[FeedPosition],
                                           dbRunner: DbRunner,
                                           tablename: String          = "feedpointers",
                                           schemaName: Option[String] = None)
    extends FeedPositionRepo(dbRunner, tablename, schemaName) {

  override def find(feednaam: String)(implicit ec: ExecutionContext): DBIO[Option[FeedPosition]] = {
    for {
      feedPosDB <- super.find(feednaam)
      feedPosToUse <- if (feedPosDB.isDefined) { DBIO.successful(feedPosDB) } else { customPositieDbioProvider().map(Option.apply) }
    } yield feedPosToUse
  }
}
