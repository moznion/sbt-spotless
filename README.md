# sbt-spotless [![Scala CI](https://github.com/moznion/sbt-spotless/workflows/Scala%20CI/badge.svg)](https://github.com/moznion/sbt-spotless/actions?query=workflow%3A%22Scala+CI%22) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.moznion.sbt/sbt-spotless/badge.svg?kill_cache=1)](https://search.maven.org/artifact/net.moznion.sbt/sbt-spotless/)

An sbt plugin for [Spotless](https://github.com/diffplug/spotless) code formatter/checker.

## Notice

**This project is under development status.**

There is the possibility that everything will be changed without notice.

## Installation

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.moznion.sbt/sbt-spotless/badge.svg?kill_cache=1)](https://search.maven.org/artifact/net.moznion.sbt/sbt-spotless/)

in your `plugins.sbt`:

```
addSbtPlugin("net.moznion.sbt" % "sbt-spotless" % "0.0.1-alpha1")
```

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
|------|------|
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

### Some formatter cannot resolve the dynamic dependency

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

## For developers

### How to run tests

```
SPOTLESS_SBT_TEST_DEPDIR=$(pwd)/.dyn_dep_for_dev sbt clean scripted
```

`SPOTLESS_SBT_TEST_DEPDIR` is an environment variable to specify a directory for dynamic dependency cache.

If you would like to clear cache, please remove the contents that is in the directory.

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

