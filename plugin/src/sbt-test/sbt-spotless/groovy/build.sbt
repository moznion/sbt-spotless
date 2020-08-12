import net.moznion.sbt.spotless.config._

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.12.12",
    spotless := SpotlessConfig(
      dynamicDependencyWorkingDir = file(System.getProperty("plugin.scriptedTestDepDir")),
      dynamicDependencyCacheDir = file(System.getProperty("plugin.scriptedTestDepDir") + "/.spotless"),
    ),
    spotlessGroovy := GroovyConfig(
      target = Seq("src/**/*.groovy"),
      grEclipse = GrEclipseConfig(
        version = "4.13.0",
      ),
    ),
    libraryDependencies ++= List(
      "org.eclipse.platform" % "org.eclipse.equinox.app" % "1.3.600", // FIXME workaround for dynamic dependency resolution
      "com.diffplug.spotless" % "spotless-eclipse-groovy" % "3.5.0", // FIXME workaround for dynamic dependency resolution
    ),
  )
