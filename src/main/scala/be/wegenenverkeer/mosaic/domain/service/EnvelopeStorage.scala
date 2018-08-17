package be.wegenenverkeer.mosaic.domain.service

import java.io.{File, FileWriter}

import be.wegenenverkeer.mosaic.domain.model.CRS
import be.wegenenverkeer.mosaic.util.Logging
import org.geolatte.geom.{C2D, Envelope}
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.util.Try

trait EnvelopeStorage {

  def write(envelope: Envelope[C2D]): Future[Unit]

  def read(): Future[List[Envelope[C2D]]]

}

class FileEnvelopeStorage()(implicit exc: ExecutionContext) extends EnvelopeStorage with Logging {

  val tmpDir: File  = File.createTempFile("tmp", "tmp").getParentFile
  val baseDir: File = new File(tmpDir, "mosaic")

  baseDir.mkdirs()

  override def write(envelope: Envelope[C2D]): Future[Unit] = {

    Future {
      val min       = envelope.lowerLeft()
      val max       = envelope.upperRight()
      val jsonArray = Json.stringify(Json.toJson(Seq(min.getX, min.getY, max.getX, max.getY)))

      val file       = File.createTempFile("mosaic", ".json", baseDir)
      val fileWriter = new FileWriter(file)
      fileWriter.write(jsonArray)
      fileWriter.close()
    }

  }

  override def read(): Future[List[Envelope[C2D]]] = {

    Future {
      baseDir.listFiles().toList.sortBy(_.lastModified).flatMap { file =>
        val t = Try {
          val content = Source.fromFile(file).mkString

          val coords                              = Json.parse(content).validate[Seq[Double]].getOrElse(throw new Exception("Could not parse envelope"))
          val minX :: minY :: maxX :: maxY :: Nil = coords

          new Envelope[C2D](minX, minY, maxX, maxY, CRS.LAMBERT72)
        }
        t.failed.foreach { t =>
          logger.warn(s"Fout bij het converteren van $file naar envelope.", t)
        }
        t.toOption.toList
      }
    }

  }

}
