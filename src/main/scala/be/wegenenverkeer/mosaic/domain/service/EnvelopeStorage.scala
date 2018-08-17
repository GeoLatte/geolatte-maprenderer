package be.wegenenverkeer.mosaic.domain.service

import java.io.{File, FileFilter, FileWriter}

import be.wegenenverkeer.mosaic.domain.model.CRS
import be.wegenenverkeer.mosaic.util.Logging
import org.geolatte.geom.{C2D, Envelope}
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.util.Try

trait EnvelopeStorage {

  def schrijf(envelope: Envelope[C2D]): Future[Unit]

  def lees(limit: Int = 10, uitgezonderd: Set[String]): Future[List[EnvelopeFile]]

  def verwijder(fileRef: String): Future[Unit]

  def aantalItemsBeschikbaar(): Future[Long]

}

case class EnvelopeFile(envelope: Envelope[C2D], fileRef: String)

class FileEnvelopeStorage()(implicit exc: ExecutionContext) extends EnvelopeStorage with Logging {

  val tmpDir: File  = File.createTempFile("tmp", "tmp").getParentFile
  val baseDir: File = new File(tmpDir, "mosaic")

  baseDir.mkdirs()

  override def schrijf(envelope: Envelope[C2D]): Future[Unit] = Future {

    val min       = envelope.lowerLeft()
    val max       = envelope.upperRight()
    val jsonArray = Json.stringify(Json.toJson(Seq(min.getX, min.getY, max.getX, max.getY)))

    val file       = File.createTempFile("mosaic", ".json", baseDir)
    val fileWriter = new FileWriter(file)
    fileWriter.write(jsonArray)
    fileWriter.close()

  }

  override def lees(limit: Int, uitgezonderd: Set[String]): Future[List[EnvelopeFile]] = Future {

    val filter = new FileFilter {
      override def accept(pathname: File): Boolean = {
        pathname.isFile && !uitgezonderd.contains(pathname.getPath)
      }
    }

    baseDir.listFiles(filter).toList.sortBy(_.lastModified).take(limit).flatMap { file =>
      val t = Try {
        val content = Source.fromFile(file).mkString

        val coords = Json.parse(content).validate[Seq[Double]].getOrElse(throw new Exception("Could not parse envelope"))

        val minX :: minY :: maxX :: maxY :: Nil = coords

        EnvelopeFile(new Envelope[C2D](minX, minY, maxX, maxY, CRS.LAMBERT72), file.getPath)
      }
      t.failed.foreach { t =>
        logger.warn(s"Fout bij het converteren van $file naar envelope.", t)
      }
      t.toOption.toList
    }

  }

  override def verwijder(fileRef: String): Future[Unit] = Future {
    new File(fileRef).delete()
  }

  override def aantalItemsBeschikbaar(): Future[Long] = Future {
    baseDir.listFiles().count(_.isFile)
  }
}
