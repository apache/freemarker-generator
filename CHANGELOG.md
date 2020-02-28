# Change Log

All notable changes to this project will be documented in this file. We try to adhere to https://github.com/olivierlacan/keep-a-changelog.

## 0.1.0-SNAPSHOT

### Added
* [FREEMARKER-129] Support document exclude pattern in addition to include pattern
* [FREEMARKER-129] User-defined parameters are passed as `-Pkey=value` instead of using system properties
* [FREEMARKER-129] Add `freemarker-generator-maven-plugin-sample`
* [FREEMARKER-129] Migrate `freemarker-cli` into `freemarker-generator` project (see [https://github.com/sgoeschl/freemarker-cli](https://github.com/sgoeschl/freemarker-cli))

### Changed
* [FREEMARKER-129] Use `freemarker.configuration.setting` in `freemarker-cli.properties` to configure FreeMarker
* [FREEMARKER-129] Provide a `toString()` metheod for all tools
* [FREEMARKER-129] Use version "0.X.Y" to cater for API changes according to [Semantic Versioning](https://semver.org)
* [FREEMARKER-129] Change artifact `freemarker-maven-plugin` to `freemarker-generator-maven-plugin`
* [FREEMARKER-128] Update `freemarker-maven-plugin` to Apache FreeMarker 2.3.29

### Deleted

### Fixed 
* [FREEMARKER-127] Site build fails with missing "org/apache/maven/doxia/siterenderer/DocumentContent"

[FREEMARKER-127]: https://issues.apache.org/jira/browse/FREEMARKER-127
[FREEMARKER-128]: https://issues.apache.org/jira/browse/FREEMARKER-128
[FREEMARKER-129]: https://issues.apache.org/jira/browse/FREEMARKER-129