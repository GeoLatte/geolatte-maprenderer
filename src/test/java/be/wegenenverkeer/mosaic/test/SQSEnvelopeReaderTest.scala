package be.wegenenverkeer.mosaic.test

import be.wegenenverkeer.mosaic.domain.service.storage.{EnvelopeReader, SQSEnvelopeReader}
import org.scalatest.FunSuite

class SQSEnvelopeReaderTest extends FunSuite {

  test("Message parsing") {

    val messageString =
      """{
      "Type" : "Notification",
      "MessageId" : "aa85e1d5-d1c4-563a-9e3b-b5ddf1029887",
      "TopicArn" : "arn:aws:sns:eu-west-1:162510209540:mosaic-dev",
      "Message" : "[159413.167133623,219518.91,159415.43,219522.957806326]",
      "Timestamp" : "2018-09-04T05:56:22.940Z",
      "SignatureVersion" : "1",
      "Signature" : "TauJOXfyH+LuzSfcu3O8veLazHNajw7AU4S3pOxS3xHWh40LhhAgi6vGdVUTVINJVGAMlLyI1uuvtl7vN1KYUWipI/kR0UuATLdCCDWj6LdAmWlkR/jxd9qMYAOeI2cmxs0bSx2vAmgSuP/7cIPO1VhvUCDdOrPRX4NdnGbn1QNDzCuprUCDAMRQsTlIpw97aEtBo/+1h+npsXL8tGGlQWIHysRl3TA6gQhoM7SVoMjLY7Npm6JsTtFJ5QmHGejXOsFQRwrGxYvbRGk0lhwNSQOkWkw8+4sUHz9kE0/T8zZv2wAKYD4GqbQv+A81KvZ7YQA4/GMEeEnIm4ueYBTmxw==",
      "SigningCertURL" : "https://sns.eu-west-1.amazonaws.com/SimpleNotificationService-eaea6120e66ea12e88dcd8bcbddca752.pem",
      "UnsubscribeURL" : "https://sns.eu-west-1.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:eu-west-1:162510209540:mosaic-dev:d2fa8ae0-079f-4035-ab47-b9a9c2d67364"
    }"""

    val messageTry = SQSEnvelopeReader.parseSnsNotificationMessage(messageString)

    assert(messageTry.isSuccess)
    val message = messageTry.get

    val envelopeTry = EnvelopeReader.parseEnvelopeString(message.Message)

    assert(envelopeTry.isSuccess)
    val envelope = envelopeTry.get

    assert(envelope.lowerLeft.getX === 159413.167133623)
    assert(envelope.lowerLeft.getY === 219518.91)
    assert(envelope.upperRight.getX === 159415.43)
    assert(envelope.upperRight.getY === 219522.957806326)

  }

}
