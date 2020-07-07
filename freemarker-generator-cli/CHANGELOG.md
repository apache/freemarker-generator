# Change Log

All notable changes to this project will be documented in this file. We try to adhere to https://github.com/olivierlacan/keep-a-changelog.

## 0.1.0-SNAPSHOT

### Added
* [FREEMARKER-149] Support multiple template transformations on the command line
* [FREEMARKER-144] Proof Of Concept for providing DataFrames
* [FREEMARKER-142] Support Transformation Of Directories
* [FREEMARKER-139] freemarker-cli: Provide GsonTool to align with Maven plugin
* Environment variables can bes passed as `DataSource`
* [FREEMARKER-135] Support user-supplied names for `DataSource` on the command line
* [FREEMARKER-129] Support `DataSource` exclude pattern in addition to include pattern
* [FREEMARKER-129] User-defined parameters are passed as `-Pkey=value` instead of using system properties
* [FREEMARKER-129] Migrate `freemarker-cli` into `freemarker-generator` project (see [https://github.com/sgoeschl/freemarker-cli](https://github.com/sgoeschl/freemarker-cli))

### Changed
* Removing `DataSources.first` and use `DataSources.get(0)` instead
* [FREEMARKER-146] Cleanly separate example templates and data from user-supplied content
* `DataSource` use `uri` instead of `location`
* [FREEMARKER-138] freemarker-generator: Rename `Datasource` to `DataSource`
* [FREEMARKER-136] Fix broken `site:stage` build
* [FREEMARKER-134] Rename `Document` to `Datasource` which also changes `--document` to `--datasource`
* [FREEMARKER-129] Use `freemarker.configuration.setting` in `freemarker-cli.properties` to configure FreeMarker
* [FREEMARKER-129] Provide a `toString()` method for all tools
* [FREEMARKER-129] Use version "0.X.Y" to cater for API changes according to [Semantic Versioning](https://semver.org)

### Fixed 
* [FREEMARKER-151] Ensure that build and and examples are running on Windows
* [FREEMARKER-147] Complete Maven site documentation
* [FREEMARKER-127] Site build fails with missing "org/apache/maven/doxia/siterenderer/DocumentContent"

[FREEMARKER-127]: https://issues.apache.org/jira/browse/FREEMARKER-127
[FREEMARKER-128]: https://issues.apache.org/jira/browse/FREEMARKER-128
[FREEMARKER-129]: https://issues.apache.org/jira/browse/FREEMARKER-129
[FREEMARKER-134]: https://issues.apache.org/jira/browse/FREEMARKER-134
[FREEMARKER-135]: https://issues.apache.org/jira/browse/FREEMARKER-135
[FREEMARKER-136]: https://issues.apache.org/jira/browse/FREEMARKER-136
[FREEMARKER-138]: https://issues.apache.org/jira/browse/FREEMARKER-138
[FREEMARKER-139]: https://issues.apache.org/jira/browse/FREEMARKER-139
[FREEMARKER-142]: https://issues.apache.org/jira/browse/FREEMARKER-142
[FREEMARKER-144]: https://issues.apache.org/jira/browse/FREEMARKER-144
[FREEMARKER-146]: https://issues.apache.org/jira/browse/FREEMARKER-146
[FREEMARKER-147]: https://issues.apache.org/jira/browse/FREEMARKER-147
[FREEMARKER-149]: https://issues.apache.org/jira/browse/FREEMARKER-149
[FREEMARKER-151]: https://issues.apache.org/jira/browse/FREEMARKER-151