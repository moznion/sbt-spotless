import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

def getVersionSpecificScalacOptions(scalaVersion: String): Seq[String] = {
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, scalaMinor)) if scalaMinor == 12 =>
      Seq(
        "-Xfatal-warnings",
        "-Ywarn-unused-import"
      )
    case _ => Seq()
  }
}

lazy val sbtSpotless = project.in(file(".")).aggregate(plugin).settings(skip in publish := true)

lazy val plugin = project
  .withId("sbt-spotless")
  .in(file("plugin"))
  .enablePlugins(SbtPlugin)
  .enablePlugins(ScriptedPlugin)
  .enablePlugins(AutomateHeaderPlugin)
  .settings(
    organization := "net.moznion.sbt",
    organizationName := "moznion.net",
    organizationHomepage := Some(url("https://moznion.net")),
    name := "sbt-spotless",
    homepage := Some(url("https://github.com/moznion/sbt-spotless")),
    startYear := Some(2020),
    description := "An sbt plugin for spotless",
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    developers := List(
      Developer(
        id = "moznion",
        name = "Taiki Kawakami",
        email = "moznion@gmail.com",
        url = url("https://moznion.net")
      )
    ),
    crossScalaVersions := Seq("2.10.7", "2.12.11"),
    crossSbtVersions := List("0.13.11", "1.3.0"),
    scalacOptions ++= (Seq(
      "-encoding",
      "UTF-8",
      "-unchecked",
      "-deprecation",
      "-feature",
      "-Xlint"
    ) ++ getVersionSpecificScalacOptions(scalaVersion.value)),
    javacOptions ++= Seq("-encoding", "UTF-8"),
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    scalafmtOnCompile := true,
    libraryDependencies ++= Dependencies.sbtSpotless(scalaVersion.value),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    publishTo := sonatypePublishToBundle.value,
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.10" => "0.13.11"
        case "2.12" => "1.2.0"
      }
    },
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      releaseStepCommand("scripted"),
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommandAndRemaining("+publishSigned"),
      releaseStepCommand("sonatypeBundleRelease"),
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  )
