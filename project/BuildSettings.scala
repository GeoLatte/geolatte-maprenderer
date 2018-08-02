import sbt._
import sbt.Keys._
import com.typesafe.sbt.packager.Keys._


object BuildSettings {

  val scalacBuildOptions = Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-Xlint:-infer-any",
    "-encoding",
    "UTF-8",
    "-target:jvm-1.8",
    "-Ydelambdafy:method",
    "-Ypartial-unification"
  )

  def projSettings(dependencies:Seq[ModuleID] = Seq()) = {
    parallelExecution := false
    testForkedParallel := false
    defaultSettings ++ Seq(
      libraryDependencies ++= dependencies
    )
  }

  val defaultSettings = Seq(
      maintainer := "AWV",
      scalacOptions := scalacBuildOptions
    ) 

}
