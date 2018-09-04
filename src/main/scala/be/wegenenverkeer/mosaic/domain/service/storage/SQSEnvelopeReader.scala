package be.wegenenverkeer.mosaic.domain.service.storage

import akka.stream.Materializer
import be.wegenenverkeer.mosaic.util.Logging
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.amazonaws.services.sqs.{AmazonSQS, AmazonSQSClientBuilder}
import play.api.libs.json.{Format, Json}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class SQSEnvelopeReader(queueUrl: String, awsCredentialsProvider: AWSCredentialsProvider)(implicit exc: ExecutionContext, mat: Materializer)
    extends EnvelopeReader
    with Logging {

  import SQSEnvelopeReader._
  import EnvelopeReader._

  val sqsClient: AmazonSQS = {
    AmazonSQSClientBuilder
      .standard()
      .withRegion("eu-west-1")
      .withCredentials(awsCredentialsProvider)
      .build()
  }

  override def lees(limit: Int, uitgezonderd: Set[String]): Future[List[EnvelopeFile]] = Future {

    val request =
      new ReceiveMessageRequest()
        .withQueueUrl(queueUrl)
        .withMaxNumberOfMessages(Math.min(10, limit)) // Valid values are 1 to 10. Default is 1.

    val receiveMessageResult = sqsClient.receiveMessage(request)

    receiveMessageResult.getMessages.asScala.toList
      .filterNot { message =>
        uitgezonderd.contains(message.getMessageId)
      }
      .flatMap { message =>
        val envelopeTry = for {
          snsMessage <- parseSnsNotificationMessage(message.getBody)
          envelope <- parseEnvelopeString(snsMessage.Message)
        } yield envelope

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

  override def verwijder(envelopeFile: EnvelopeFile): Future[Unit] = Future {
    sqsClient.deleteMessage(queueUrl, envelopeFile.verwijderRef)
  }

  override def aantalItemsBeschikbaar(): Future[Long] = Future {
    val request =
      new ReceiveMessageRequest()
        .withQueueUrl(queueUrl)
        .withVisibilityTimeout(0) // messages moeten niet onzichtbaar worden
        .withMaxNumberOfMessages(10) // Valid values are 1 to 10. Default is 1.

    sqsClient.receiveMessage(request).getMessages.size
  }

}

case object SQSEnvelopeReader {

  case class SnsNotificationMessage(MessageId: String, TopicArn: String, Timestamp: String, Message: String)

  case object SnsNotificationMessage {
    implicit val format: Format[SnsNotificationMessage] = Json.format[SnsNotificationMessage]
  }

  def parseSnsNotificationMessage(messageString: String): Try[SnsNotificationMessage] = {
    Try {
      Json
        .parse(messageString)
        .validate[SnsNotificationMessage]
        .getOrElse(throw new Exception(s"Could not parse snsNotificationMessage $messageString"))
    }
  }

}
