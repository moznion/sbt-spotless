lazy val sbtSpotless = project.in(file(".")).aggregate(plugin).settings(skip in publish := true)

lazy val plugin = project
  .withId("sbt-spotless")
  .in(file("plugin"))
  .enablePlugins(SbtPlugin)
  .enablePlugins(ScriptedPlugin)
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
      Developer(id = "moznion", name = "Taiki Kawakami", email = "moznion@gmail.com", url = url("https://moznion.net"))
    ),
    crossSbtVersions := List("1.3.0"),
    scalacOptions ++= Seq(
      "-encoding",
      "UTF-8",
      "-unchecked",
      "-deprecation",
      "-feature",
      "-Xlint",
      "-Xfatal-warnings",
      "-Ywarn-unused-import",
    ),
    javacOptions ++= Seq("-encoding", "UTF-8"),
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    scalafmtOnCompile := true,
    libraryDependencies ++= Dependencies.sbtSpotless,
    publishMavenStyle := true,
    publishArtifact in Test := false,
    publishTo := sonatypePublishToBundle.value,
  )
  .enablePlugins(AutomateHeaderPlugin)
