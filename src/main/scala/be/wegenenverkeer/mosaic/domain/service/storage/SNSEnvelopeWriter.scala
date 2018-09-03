package be.wegenenverkeer.mosaic.domain.service.storage

import akka.stream.Materializer
import be.wegenenverkeer.mosaic.util.Logging
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.services.sns._
import org.geolatte.geom.{C2D, Envelope}

import scala.concurrent.{ExecutionContext, Future}

class SNSEnvelopeWriter(topicArn: String, awsCredentialsProvider: AWSCredentialsProvider)(implicit exc: ExecutionContext, mat: Materializer)
    extends EnvelopeWriter
    with Logging {

  val snsClient: AmazonSNS = {
    AmazonSNSClientBuilder
      .standard()
      .withRegion("eu-west-1")
      .withCredentials(awsCredentialsProvider)
      .build()
  }

  override def schrijf(envelope: Envelope[C2D]): Future[Unit] = Future {
    val message       = envelopeToString(envelope)
    val publishResult = snsClient.publish(topicArn, message)
    logger.info(s"Published message ${publishResult.getMessageId}")
  }
}
