package be.wegenenverkeer.mosaic.infrastructure.apploader

import akka.stream.Materializer
import be.wegenenverkeer.mosaic.domain.service.storage._
import com.amazonaws.auth.AWSCredentialsProvider
import play.api.Configuration

import scala.concurrent.ExecutionContext

trait EnvelopeStorageModule extends EnvelopeReaderModule with EnvelopeWriterModule

trait EnvelopeReaderModule {
  def envelopeReader: EnvelopeReader
}

trait EnvelopeWriterModule {
  def envelopeWriter: EnvelopeWriter
}

trait AwsEnvelopeStorageModule extends AwsEnvelopeWriterModule with AwsEnvelopeReaderModule

trait AwsEnvelopeWriterModule extends EnvelopeWriterModule {

  implicit def executionContext: ExecutionContext

  implicit def materializer: Materializer

  def configuration: Configuration

  def awsCredentialsProvider: AWSCredentialsProvider

  lazy val envelopeWriter: EnvelopeWriter = {
    val snsTopic = configuration
      .getOptional[String]("aws.sns.topic")
      .getOrElse(sys.error("aws.sns.topic required"))
    new SNSEnvelopeWriter(snsTopic, awsCredentialsProvider)
  }

}

trait AwsEnvelopeReaderModule extends EnvelopeReaderModule {

  implicit def executionContext: ExecutionContext

  implicit def materializer: Materializer

  def configuration: Configuration

  def awsCredentialsProvider: AWSCredentialsProvider

  lazy val envelopeReader: EnvelopeReader = {
    val sqsQueue = configuration
      .getOptional[String]("aws.sqs.queue")
      .getOrElse(sys.error("aws.sqs.queue required"))
    new SQSEnvelopeReader(sqsQueue, awsCredentialsProvider)
  }

}

trait FileSystemEnvelopeStorageModule extends EnvelopeStorageModule {

  implicit def executionContext: ExecutionContext

  lazy val envelopeStorage: EnvelopeStorage = new FileEnvelopeStorage()

  lazy val envelopeReader: EnvelopeReader = envelopeStorage

  lazy val envelopeWriter: EnvelopeWriter = envelopeStorage

}
