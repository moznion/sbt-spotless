import net.moznion.sbt.spotless.config._

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.13.13",
    spotless := SpotlessConfig(
      noFailOnViolated = true,
      dynamicDependencyWorkingDir = file(System.getProperty("plugin.scriptedTestDepDir")),
      dynamicDependencyCacheDir = file(System.getProperty("plugin.scriptedTestDepDir") + "/.spotless"),
    ),
    spotlessJava := JavaConfig(
      googleJavaFormat = GoogleJavaFormatConfig(version = "1.7")
    ),
  )
