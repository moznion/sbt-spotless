# sbt-spotless [![Scala CI](https://github.com/moznion/sbt-spotless/workflows/Scala%20CI/badge.svg)](https://github.com/moznion/sbt-spotless/actions?query=workflow%3A%22Scala+CI%22) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.moznion.sbt/sbt-spotless/badge.svg?kill_cache=1)](https://search.maven.org/artifact/net.moznion.sbt/sbt-spotless/)

An sbt plugin for [Spotless](https://github.com/diffplug/spotless) code formatter/checker.

## Installation

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.moznion.sbt/sbt-spotless/badge.svg?kill_cache=1)](https://search.maven.org/artifact/net.moznion.sbt/sbt-spotless/)

in your `plugins.sbt`:

```
addSbtPlugin("net.moznion.sbt" % "sbt-spotless" % "0.0.1")
```

### Supported sbt versions

- sbt 1.3
- sbt 0.13

This plugin supports the legacy sbt 0.13, but the legacy one has some issue on dynamic dependency resolution (see also [known issue](#known-issues)) and there might be some unexpected behavior potentially. Also an author doesn't motivate to maintenance the "legacy sbt" in the future continuously/positively, so there will a possibility to give up the support without notice. So I highly recommend considering to upgrade the sbt to 1.3.

## Usage

### Check the code format

```
$ sbt spotlessCheck
```

### Apply the code formatter

```
$ sbt spotlessApply
```

## Supported formatters

|Format|Implemented|
|------|:----:|
|Java|✅|
|Scala|✅|
|Kotlin|✅|
|Groovy|✅|
|cpp|✅|
|SQL|✅|

Supporting other formats is under working. And of course, pull-request is welcome.

## Configurations

Please refer to the following wiki page: [Configurations](https://github.com/moznion/sbt-spotless/wiki/Configurations)

## Dynamic dependency

Spotless tries to reduce the static dependencies of various formatters, so spotless resolves the dependencies for formatters on-the-fly if the formatter is needed.

Basically, Spotless runner resolves the dynamic dependencies every time, that is not efficient. So this plugin caches the dynamic dependencies into files once that has resolved deps. And after that, it runs Spotless with cached libraries.

## Known issues

### (sbt 1.3) Some formatter cannot resolve the dynamic dependency

On some formatter, this plugin (i.e. Ivy2) cannot resolve dependencies of the formatter dynamically so it needs to declare the dependencies explicitly in your `build.sbt`.

#### Groovy

```scala
libraryDependencies ++= List(
  "org.eclipse.platform" % "org.eclipse.equinox.app" % "1.3.600", // FIXME workaround for dynamic dependency resolution
  "com.diffplug.spotless" % "spotless-eclipse-groovy" % "3.5.0", // FIXME workaround for dynamic dependency resolution
),
```

#### cpp

```scala
libraryDependencies ++= List(
  "org.eclipse.platform" % "org.eclipse.equinox.app" % "1.3.600", // FIXME workaround for dependency resolution
  "com.diffplug.spotless" % "spotless-eclipse-cdt" % "9.9.0", // FIXME workaround for dependency resolution
),
```

### (sbt 0.13) Some formatter cannot resolve the dynamic dependency

In sbt 0.13, highly recommend disabling dynamic dependency resolution by setting [disableDynamicDependencyResolving](https://github.com/moznion/sbt-spotless/wiki/Plugin-Configuration#disabledynamicdependencyresolving-boolean) `true` and specify the required dependencies explicitly in your `build.sbt`.

For example, in case of Java, let's specify like `"com.google.googlejavaformat" % "google-java-format" % "1.7"`, and in case of Scala, please specify `"org.scalameta" %% "scalafmt-core" % "2.0.1"`.

## For developers

### How to run tests

```
sbt clean scripted
```

This scripted test caches the result of resolved dynamic dependency.

If you would like to clear the cache, please remove the contents that are in a directory: `plugin/target/.dyn-dep-test/`. FYI, `sbt clean` also clears the cache.

### How to release this plugin to maven central

```
sbt release
```

### How to try this plugin on local

```
$ sbt publishLocal
```

## Author

moznion (<moznion@gmail.com>)

## License

```
Copyright 2020 moznion (https://moznion.net)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

