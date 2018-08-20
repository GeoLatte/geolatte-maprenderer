package be.wegenenverkeer.mosaic.domain.service.storage

import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import java.util.concurrent.atomic.AtomicLong

import akka.stream.Materializer
import akka.stream.scaladsl.Source
import be.wegenenverkeer.mosaic.util.Logging
import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.event.{ProgressEvent, ProgressEventType, ProgressListener}
import com.amazonaws.services.s3.model._
import com.amazonaws.services.s3.transfer.internal.S3ProgressListener
import com.amazonaws.services.s3.transfer.{PersistableTransfer, TransferManager, TransferManagerBuilder}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import org.geolatte.geom.{C2D, Envelope}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Random, Success, Try}

class S3EnvelopeStorage(s3Bucket: S3Bucket, awsCredentialsProvider: AWSCredentialsProvider)(implicit exc: ExecutionContext,
                                                                                            mat: Materializer)
    extends EnvelopeStorage
    with Logging {

  private val UTF8 = Charset.forName("UTF8")

  private val fileNameGen = new AtomicLong(System.currentTimeMillis())

  val s3Client: AmazonS3 = {

    val clientConfig = new ClientConfiguration()
    clientConfig.setMaxConnections(50)
    clientConfig.setClientExecutionTimeout(5 * 1000 * 60) //5 minutes

    AmazonS3ClientBuilder
      .standard()
      .withRegion("eu-west-1")
      .withCredentials(awsCredentialsProvider)
      .withClientConfiguration(clientConfig)
      .build()
  }

  val transferManager: TransferManager = TransferManagerBuilder.standard().withS3Client(s3Client).build()

  private def getRealId(id: String): String = {
    s3Bucket.objectPrefix + "/" + id
  }

  override def schrijf(envelope: Envelope[C2D]): Future[Unit] = {
    val content  = envelopeToString(envelope)
    val fileName = fileNameGen.getAndIncrement() + ".json"

    val realId = getRealId(fileName)

    val bytes = content.getBytes(UTF8)

    val metadata = new ObjectMetadata()
    metadata.setContentLength(bytes.length)

    val objectRequest = new PutObjectRequest(s3Bucket.bucketName, realId, new ByteArrayInputStream(bytes), metadata)

    val uploadPromise = Promise[Unit]()

    // progress listener om Promise aan te vullen
    val progressListener = new S3ProgressListener() {

      def progressChanged(progressEvent: ProgressEvent): Unit = {

        // convenience method om de Promise aan te vullen met een RuntimeException
        def completeFailure(msg: String) = {
          if (!uploadPromise.isCompleted) {
            uploadPromise.tryComplete(Failure(new RuntimeException(msg)))
          }
        }

        progressEvent.getEventType match {
          case ProgressEventType.TRANSFER_COMPLETED_EVENT =>
            uploadPromise.complete(Success(()))

          // Failure events
          case ProgressEventType.TRANSFER_FAILED_EVENT =>
            completeFailure("Transfer naar S3 gefaald!")
          case ProgressEventType.TRANSFER_CANCELED_EVENT =>
            completeFailure("Transfer naar S3 geannuleerd!")
          case ProgressEventType.TRANSFER_PART_FAILED_EVENT =>
            completeFailure("Transfer part naar S3 gefaald!")
          case ProgressEventType.CLIENT_REQUEST_FAILED_EVENT =>
            completeFailure("Klant request naar S3 gefaald!")

          case _ => () // doe niets bij andere events
        }

      }
      override def onPersistableTransfer(persistableTransfer: PersistableTransfer): Unit = Unit
    }

    transferManager.upload(objectRequest, progressListener)

    // Promise to Future
    uploadPromise.future
  }

  private def listFileKeys(limit: Int, uitgezonderd: Set[String]): Future[Seq[String]] = Future {
    val fileSummaries = mutable.Buffer[S3ObjectSummary]()

    def fetchNextBatch(continuationToken: Option[String]): ListObjectsV2Result = {
      val request =
        new ListObjectsV2Request()
          .withBucketName(s3Bucket.bucketName)
          .withPrefix(s3Bucket.objectPrefix)
          .withMaxKeys((limit * 1.5).toInt) // standaard meer ophalen, wordt nadien gefilterd en terug gelimiteerd
          .withContinuationToken(continuationToken.orNull)

      val result = s3Client.listObjectsV2(request)

      fileSummaries ++= result.getObjectSummaries.asScala
        .filterNot(summary => uitgezonderd.contains(summary.getKey))
        .sortBy(_.getLastModified)
        .take(limit)

      result
    }

    var result = fetchNextBatch(None)

    while (fileSummaries.size < limit && result.getNextContinuationToken != null) {
      result = fetchNextBatch(Option(result.getNextContinuationToken))
    }

    fileSummaries.sortBy(_.getLastModified).map(_.getKey)
  }

  private def downloadEnvelopeFile(fileKey: String): Future[Try[EnvelopeFile]] = Future {
    val contentStream = s3Client.getObject(s3Bucket.bucketName, fileKey).getObjectContent
    val source        = scala.io.Source.fromInputStream(contentStream, UTF8.name())
    val content       = source.mkString
    source.close()

    val envelope = stringToEnvelope(content)
    envelope.failed.foreach { t =>
      logger.warn(s"Fout bij het converteren van $fileKey naar envelope.", t)
    }
    envelope.map(envelope => EnvelopeFile(envelope, fileKey))
  }

  override def lees(limit: Int, uitgezonderd: Set[String]): Future[List[EnvelopeFile]] = {

    Source
      .fromFutureSource {
        listFileKeys(limit, uitgezonderd).map(seq => Source.fromIterator(() => seq.iterator))
      }
      .mapAsync(4)(downloadEnvelopeFile)
      .map {
        case Success(envelopeFile) => envelopeFile
      }
      .runFold(List[EnvelopeFile]()) { (acc, envelopeFile) =>
        envelopeFile :: acc
      }

  }

  override def verwijder(fileRef: String): Future[Unit] = Future {
    s3Client.deleteObject(s3Bucket.bucketName, fileRef)
  }

  override def aantalItemsBeschikbaar(): Future[Long] = {
    listFileKeys(10000, Set()).map(_.size)
  }
}

/**
  *
  * @param bucketName naam van de bucket
  * @param objectPrefix De objectprefix wordt gebruikt om alle bestanden in 1 "dir" te zetten in S3 ipv in de root
  *
  */
case class S3Bucket(bucketName: String, objectPrefix: String)
