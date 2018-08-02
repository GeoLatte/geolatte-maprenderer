/**
  * The UI build is entirely based on npm (webpack). This sbt config ensures that the UI is built and packaged into the play application.
  */
import java.io.File
import scala.sys.process.Process

lazy val webpack: TaskKey[Unit] = taskKey[Unit]("Build and copy webpack artifacts")

/**
  * Roept npm build uit
  */
def buildNpm() = {

  val cmd = Seq("bash", "-c", "npm run build")

  println(s"Building UI... (cmd: ${cmd.mkString(" ")})")

  val res = Process(cmd, new File("modules/mosaic-ui")).!

  if (res != 0) throw new Exception("Webpack build failed!")

  println("Webpack build done!")
}

webpack := {

  buildNpm()

  println(s"Copying webpack artifacts")

  val versionStr = version.value

  val to = target.value / "webpack"

  to.mkdirs

  val from = new File("modules/mosaic-ui/target/dist")

  IO.copyDirectory(from, to)

  println(s"$from -> $to ...done.")
}

unmanagedResourceDirectories in Assets += target.value / "webpack"

Keys.`package` := ((Keys.`package` in Compile) dependsOn webpack).value

dist := (dist dependsOn Keys.`package`).value
