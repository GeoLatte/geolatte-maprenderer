package be.wegenenverkeer.mosaic.util
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.util.Base64

import javax.imageio.ImageIO

import scala.util.Try

trait Base64Conversion {

  def readImageFromBase64String(data: String): Option[BufferedImage] = Try(ImageIO.read(mkImageInputStream(data))).toOption

  private def mkImageInputStream(data: String): ByteArrayInputStream = {
    val decoder = Base64.getDecoder
    new ByteArrayInputStream(decoder.decode(data))
  }

}
