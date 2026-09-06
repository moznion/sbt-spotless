import sbt._

object Dependencies {
  private lazy val spotlessVersion = "4.10.2"
  def sbtSpotless(scalaVersion: String): List[ModuleID] = {
    List(
      "com.diffplug.spotless" % "spotless-lib" % spotlessVersion,
      "com.diffplug.spotless" % "spotless-lib-extra" % spotlessVersion,
    )
  }
}
