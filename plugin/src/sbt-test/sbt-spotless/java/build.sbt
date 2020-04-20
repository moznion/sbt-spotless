import net.moznion.sbt.spotless.config._

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.12.11",
    spotless := SpotlessConfig(
      dynamicDependencyWorkingDir = file(System.getenv("SPOTLESS_SBT_TEST_DEPDIR")),
      dynamicDependencyCacheDir = file(System.getenv("SPOTLESS_SBT_TEST_DEPDIR") + "/.spotless"),
    ),
    spotlessJava := JavaConfig(
      googleJavaFormat = GoogleJavaFormatConfig(version = "1.7")
    ),
  )
