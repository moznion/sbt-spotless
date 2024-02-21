import net.moznion.sbt.spotless.config._

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.13.13",
    spotless := SpotlessConfig(
      dynamicDependencyWorkingDir = file(System.getProperty("plugin.scriptedTestDepDir")),
      dynamicDependencyCacheDir = file(System.getProperty("plugin.scriptedTestDepDir") + "/.spotless"),
    ),
    spotlessScala := ScalaConfig(
      scalafmt = ScalafmtConfig(),
    ),
  )
