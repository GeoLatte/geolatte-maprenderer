package be.wegenenverkeer.mosaic.infrastructure

import be.wegenenverkeer.atomium.extension.feedconsumer.ProgressLimiter
import be.wegenenverkeer.atomium.japi.client.FeedEntry
import be.wegenenverkeer.mosaic.domain.service.{DataloaderService, VerkeersbordenService}
import be.wegenenverkeer.mosaic.infrastructure.SlickPgProfile.api._
import com.fasterxml.jackson.databind.JsonNode
import rx.lang.scala.{Observable, Subject}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise, blocking}

class DataloaderProgressLimiter(dataloaderService: DataloaderService,
                                feedUrl: String,
                                verkeersbordenService: VerkeersbordenService,
                                retryInterval: Duration = 10.seconds,
                                maxRetries: Int         = 60)(implicit exc: ExecutionContext)
    extends ProgressLimiter {


  def parseFeedPageNumber(feedPage: String): Int = {
    feedPage.split("/").filterNot(_.isEmpty).filter(_.forall(_.isDigit)).head.toInt
  }

  override def canMoveForward(entry: FeedEntry[JsonNode]): DBIO[Unit] = {

    def magVerderGaan(): Future[Boolean] = {
      dataloaderService.getDataloaderFeedPageCached(feedUrl).flatMap { feedPosition =>
        val entryPage      = parseFeedPageNumber(entry.getSelfHref)
        val dataloaderPage = parseFeedPageNumber(feedPosition.pageUrl)

        if (entryPage < dataloaderPage) {
          Future.successful(true)
        } else if (entryPage > dataloaderPage) {
          Future.successful(false)
        } else {
          // check entry id
          val entryId      = entry.getEntry.getId
          val dataloaderId = feedPosition.entryId

          val feedPage = feedPosition.pageUrl.split("/").filterNot(_.isEmpty).mkString("/")

          verkeersbordenService.getFeedPageCached(feedPage).flatMap { feedPageContent =>
            val entryPos      = feedPageContent.indexOf(entryId)
            val dataloaderPos = feedPageContent.indexOf(dataloaderId)

            if (entryPos != -1 && dataloaderPos != -1 && entryPos >= dataloaderPos) { // groter dan = ouder
              Future.successful(true)
            } else {
              Future.successful(false)
            }
          }
        }

      }
    }

    val promise = Promise[Unit]()

    val subject = Subject[Unit]()

    var retryCount = 0

    subject
      .flatMap(_ => Observable.from(magVerderGaan()))
      .foreach(
        onNext = { verder =>
          if (verder) {
            promise.success(Unit)
          } else {
            if (retryCount >= maxRetries) {
              val message =
                s"Wij willen entry ${entry.getEntry.getId} op pagina ${entry.getSelfHref} verwerken, " +
                  s"maar dataloader is niet klaar en gaat niet vooruit sinds $maxRetries keer ${retryInterval.toSeconds} sec"
              promise.failure(new Exception(message))
            } else {
              Future {
                blocking {
                  Thread.sleep(retryInterval.toMillis)
                  subject.onNext(Unit)
                }
              }
              retryCount = retryCount + 1
            }
          }
        },
        onError    = t  => promise.failure(t),
        onComplete = () => promise.failure(new Exception("Timeout"))
      )

    subject.onNext(Unit)

    DBIO.from(promise.future)

  }

}
