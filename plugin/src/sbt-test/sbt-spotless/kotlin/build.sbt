import net.moznion.sbt.spotless.config._

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.12.19",
    spotless := SpotlessConfig(
      dynamicDependencyWorkingDir = file(System.getProperty("plugin.scriptedTestDepDir")),
      dynamicDependencyCacheDir = file(System.getProperty("plugin.scriptedTestDepDir") + "/.spotless"),
    ),
    spotlessKotlin := KotlinConfig(
      target = Seq("src/**/*.kt", "test/**/*.kt"),
      ktlint = KtlintConfig(),
    )
  )
