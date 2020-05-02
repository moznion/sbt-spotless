import net.moznion.sbt.spotless.config._

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.11.12",
    spotless := SpotlessConfig(
      dynamicDependencyWorkingDir = file(System.getProperty("plugin.scriptedTestDepDir")),
      dynamicDependencyCacheDir = file(System.getProperty("plugin.scriptedTestDepDir") + "/.spotless"),
    ),
    spotlessSql := SqlConfig(
      target = Seq("src/*.sql"),
      dbeaver = DBeaverConfig(Seq()),
    ),
  )
