Apache FreeMarker Generator
=============================================================================

For documentation or to report bugs visit: [https://freemarker.apache.org](https://freemarker.apache.org)

Regarding pull requests on Github
-----------------------------------------------------------------------------

By sending a pull request you grant the Apache Software Foundation
sufficient rights to use and release the submitted work under the
Apache license. You grant the same rights (copyright license, patent
license, etc.) to the Apache Software Foundation as if you have signed
a [Contributor License Agreement](https://www.apache.org/dev/new-committers-guide.html#cla).
For contributions that are judged to be non-trivial, you will be asked
to actually signing a Contributor License Agreement.

What is Apache FreeMarker Generator?
-----------------------------------------------------------------------------

FreeMarker Generator is a set of tools that generates files based on FreeMarker
templates and data that's typically provided in files (such as JSON files) as
well. It can be used to generated source code, configuration files, etc.

Currently it can be invoked as a 

* Command-line interface `freemarker-generator-cli`
* Maven plug-in `freemarker-generator-maven-plugin`

Building Apache FreeMarker Generator
-----------------------------------------------------------------------------

To create the artefacts locally run

> mvn clean install

To build the documentation site run

> mvn clean site site:stage

To 

Licensing
-----------------------------------------------------------------------------

Apache FreeMarker Generator is licensed under the Apache License, Version 2.0.

See the LICENSE file for more details!
