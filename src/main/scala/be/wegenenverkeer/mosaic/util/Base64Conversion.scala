package be.wegenenverkeer.mosaic.util

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import java.util.Base64

import javax.imageio.ImageIO

import scala.util.Try

trait Base64Conversion {

  private val UTF8 = Charset.forName("UTF8")

  def readImageFromBase64String(data: String): Option[BufferedImage] = Try(ImageIO.read(mkImageInputStream(data))).toOption

  private def mkImageInputStream(data: String): ByteArrayInputStream = {
    val decoder = Base64.getDecoder
    new ByteArrayInputStream(decoder.decode(data))
  }

  def encodeBasicAuth(user: String, password: String): String = {
    val encoder = Base64.getEncoder
    encoder.encodeToString(s"$user:$password".getBytes(UTF8))
  }

}
