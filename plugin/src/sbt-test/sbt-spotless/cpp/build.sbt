import net.moznion.sbt.spotless.config._

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.12.12",
    spotless := SpotlessConfig(
      dynamicDependencyWorkingDir = file(System.getProperty("plugin.scriptedTestDepDir")),
      dynamicDependencyCacheDir = file(System.getProperty("plugin.scriptedTestDepDir") + "/.spotless"),
    ),
    spotlessCpp := CppConfig(
      target = Seq("src/*.h", "src/*.c"),
      eclipseCpp = EclipseCppConfig(version = "4.13.0"),
    ),
    libraryDependencies ++= List(
      "org.eclipse.platform" % "org.eclipse.equinox.app" % "1.3.600", // FIXME workaround for dependency resolution
      "com.diffplug.spotless" % "spotless-eclipse-cdt" % "9.9.0", // FIXME workaround for dependency resolution
    ),
  )
