Apache FreeMarker Generator Website
=============================================================================

Generates the FreeMarker Generator website, which also contains the documentation.
This module is used internally. The output artifact shouldn't be deployed to the
Maven Central. The output is merely to be uploaded to our website. The source code
can be part of the Apache released though.

Building
--------

This module is deliberately not built automatically when you build the top-level
module. To build successfully, first you must build the `freemarker-generator-cli`,
so that we will have `target/appassembler`. Then run the examples in
`target/appassembler`, as the documentation includes the output of some examples.
Only after these run `mvn package` in `freemarker-generator-website`.
