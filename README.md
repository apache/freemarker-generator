Apache FreeMarker Generator
=============================================================================

Introduction
-----------------------------------------------------------------------------

The [Apache Freemarker Generator](https://github.com/apache/freemarker-generator) project has quite a long and unsuccessful history

* It started when I migrated one of my pet projects from Apache Velocity & Groovy to Apache Freemarker & Java many moons ago
* I contributed the code 2022 to the Apache Software Foundation and became a committer to the Apache Freemarker project
* During the next two years I worked on Apache Freemarker Generator, but I did not see a chance to have to code officially released
* No public release, no users, no community to grow, another failed open source project
* In March 2025 I became an [Emeritus Member](https://www.apache.org/foundation/members) of the ASF
* Since I frequently require Apache Freemarker Generator myself I decided to fork and further work it

**Disclaimer - this is a fork of Apache Freemarker Generator and/but I did not change the package names so all of my private releases won't go to Maven Central** 

What is Apache FreeMarker Generator?
-----------------------------------------------------------------------------

FreeMarker Generator is a set of tools that generates files based on FreeMarker
templates and data that's typically provided in files (such as JSON files) as
well. It can be used to generate source code, configuration files, etc.

Currently, it can be invoked as a 

* Command-line interface `freemarker-generator`
* Maven plug-in `freemarker-generator-maven-plugin`

Building Apache FreeMarker Generator
-----------------------------------------------------------------------------

To create the artifacts locally run

> mvn clean install

To build the documentation site run

> mvn clean site site:stage

Licensing
-----------------------------------------------------------------------------

Apache FreeMarker Generator is licensed under the Apache License, Version 2.0.

See the LICENSE file for more details!