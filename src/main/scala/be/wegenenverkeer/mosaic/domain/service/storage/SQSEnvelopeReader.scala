package be.wegenenverkeer.mosaic.domain.service.storage

import akka.stream.Materializer
import be.wegenenverkeer.mosaic.util.Logging
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.amazonaws.services.sqs.{AmazonSQS, AmazonSQSClientBuilder}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

class SQSEnvelopeReader(queueUrl: String, awsCredentialsProvider: AWSCredentialsProvider)(implicit exc: ExecutionContext, mat: Materializer)
    extends EnvelopeReader
    with Logging {

  val sqsClient: AmazonSQS = {
    AmazonSQSClientBuilder
      .standard()
      .withRegion("eu-west-1")
      .withCredentials(awsCredentialsProvider)
      .build()
  }

  override def lees(limit: Int, uitgezonderd: Set[String]): Future[List[EnvelopeFile]] = Future {

    val request = new ReceiveMessageRequest().withQueueUrl(queueUrl).withMaxNumberOfMessages(Math.min(10, limit))

    val receiveMessageResult = sqsClient.receiveMessage(request)

    receiveMessageResult.getMessages.asScala.toList
      .filterNot { message =>
        uitgezonderd.contains(message.getMessageId)
      }
      .flatMap { message =>
        val envelopeTry = stringToEnvelope(message.getBody)

        envelopeTry.failed
          .foreach { t =>
            logger.warn(s"Fout bij het converteren van ${message.getMessageId} naar envelope.", t)
          }

        envelopeTry
          .map(envelope => EnvelopeFile(envelope, message.getMessageId, Some(message.getReceiptHandle)))
          .toOption
          .toList
      }

  }

  override def verwijder(fileRef: String): Future[Unit] = Future {
    sqsClient.deleteMessage(queueUrl, fileRef)
  }

  override def aantalItemsBeschikbaar(): Future[Long] = {
    lees(10000, Set()).map(_.size)
  }
}
