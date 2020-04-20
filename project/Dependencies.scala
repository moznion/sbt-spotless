import sbt._

object Dependencies {
  private lazy val spotlessVersion = "1.28.1"
  private lazy val betterFilesVersion = "3.8.0"
  private lazy val circeVersion = "0.13.0"
  lazy val sbtSpotless = List(
    "com.diffplug.spotless" % "spotless-lib" % spotlessVersion,
    "com.diffplug.spotless" % "spotless-lib-extra" % spotlessVersion,
    "com.github.pathikrit" %% "better-files" % betterFilesVersion,
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
  )
}
