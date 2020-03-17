# Change Log

All notable changes to this project will be documented in this file. We try to adhere to https://github.com/olivierlacan/keep-a-changelog.

## 0.1.0-SNAPSHOT

### Added
* An environment variable can bes passed as `DataSource`
* [FREEMARKER-135] Support user-supplied names for `DataSource` on the command line
* [FREEMARKER-129] Support `DataSource` exclude pattern in addition to include pattern
* [FREEMARKER-129] User-defined parameters are passed as `-Pkey=value` instead of using system properties
* [FREEMARKER-129] Add `freemarker-generator-maven-plugin-sample` for better testing of the Maven plugin
* [FREEMARKER-129] Migrate `freemarker-cli` into `freemarker-generator` project (see [https://github.com/sgoeschl/freemarker-cli](https://github.com/sgoeschl/freemarker-cli))

### Changed
* `DataSource` use `uri` instead of `location`
* [FREEMARKER-138] freemarker-generator: Rename `Datasource` to `DataSource`
* [FREEMARKER-136] Fix broken `site:stage` build
* [FREEMARKER-134] Rename `Document` to `Datasource` which also changes `--document` to `--datasource`
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
[FREEMARKER-134]: https://issues.apache.org/jira/browse/FREEMARKER-134
[FREEMARKER-135]: https://issues.apache.org/jira/browse/FREEMARKER-135
[FREEMARKER-136]: https://issues.apache.org/jira/browse/FREEMARKER-136
[FREEMARKER-138]: https://issues.apache.org/jira/browse/FREEMARKER-1386