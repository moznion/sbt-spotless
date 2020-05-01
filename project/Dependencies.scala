import sbt._

object Dependencies {
  private lazy val spotlessVersion = "1.28.1"
  def sbtSpotless(scalaVersion: String): List[ModuleID] = {
    val betterFilesVersion = CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, scalaMinor)) if scalaMinor == 12 => "3.8.0"
      case Some((2, scalaMinor)) if scalaMinor == 10 => "2.17.0"
    }

    List(
      "com.diffplug.spotless" % "spotless-lib" % spotlessVersion,
      "com.diffplug.spotless" % "spotless-lib-extra" % spotlessVersion,
      "com.github.pathikrit" %% "better-files" % betterFilesVersion,
    )
  }
}
