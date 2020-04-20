# sbt-spotless ![![Scala CI](https://github.com/moznion/sbt-spotless/workflows/Scala%20CI/badge.svg)](https://github.com/moznion/sbt-spotless/actions?query=workflow%3A%22Scala+CI%22) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.moznion.sbt/sbt-spotless/badge.svg?kill_cache=1)](https://search.maven.org/artifact/net.moznion.sbt/sbt-spotless/)

An sbt plugin for [Spotless](https://github.com/diffplug/spotless).

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

## Configurations

TODO TODO TODO

## How to try this plugin on local

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

