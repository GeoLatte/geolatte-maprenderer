package be.wegenenverkeer.mosaic.domain.service.storage

import java.io.{File, FileFilter, FileWriter}

import be.wegenenverkeer.mosaic.util.Logging
import org.geolatte.geom.{C2D, Envelope}

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.util.Try

class FileEnvelopeStorage()(implicit exc: ExecutionContext) extends EnvelopeStorage with Logging {

  import EnvelopeReader._
  import EnvelopeWriter._

  val tmpDir: File  = File.createTempFile("tmp", "tmp").getParentFile
  val baseDir: File = new File(tmpDir, "mosaic")

  baseDir.mkdirs()

  override def schrijf(envelope: Envelope[C2D]): Future[Unit] = Future {

    val file       = File.createTempFile("mosaic", ".json", baseDir)
    val fileWriter = new FileWriter(file)
    fileWriter.write(envelopeToString(envelope))
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
        val source  = Source.fromFile(file)
        val content = source.mkString
        source.close()
        content
      }.flatMap { content =>
        parseEnvelopeString(content).map(envelope => EnvelopeFile(envelope, file.getPath))
      }
      t.failed.foreach { t =>
        logger.warn(s"Fout bij het converteren van $file naar envelope.", t)
      }
      t.toOption.toList
    }

  }

  override def verwijder(envelopeFile: EnvelopeFile): Future[Unit] = Future {
    new File(envelopeFile.verwijderRef).delete()
  }

  override def aantalItemsBeschikbaar(): Future[Long] = Future {
    baseDir.listFiles().count(_.isFile)
  }
}
