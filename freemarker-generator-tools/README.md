Apache FreeMarker Generator Tools
=============================================================================

A "tool" is just a POJO (plain old java object) that is "useful" in a template and is not meant to be rendered in the output. In other words, a "tool" is meant to be used but not seen themselves (e.g. for formatting dates or numbers, url building, etc)

Design Considerations
------------------------------------------------------------------------------

* A tool shall expose a default constructor and/or a constructor taking a `Map<String, Object> settings`
* A tool processing documents shall provide a `parse(Document document)` method
* A tool shall be stateless, multi-thread safe and potentially long-lived to enable usage in a different context
* A tool shall support arbitrary large source documents and process them efficiently
* A tool shall expose a `toString()` method for documentation purposes

Available Tools
------------------------------------------------------------------------------

The following tools are currently provided

| Helper                | Description                                                                                               |
|-----------------------|-----------------------------------------------------------------------------------------------------------|
| CSVTool               | Process CSV files using [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/)              |
| ExecTool              | Execute command line tools using [Apache Commons Exec](https://commons.apache.org/proper/commons-exec/)   |
| ExcelTool             | Process Excels files (XLS, XLSX) using [Apache POI](https://poi.apache.org)                               |
| FreeMarkerTool        | Expose useful Apache FreeMarker classes                                                                   |
| GrokTool              | Process text files using [Grok](https://github.com/thekrakken/java-grok) expressions                      |
| JsonPathTool          | Process JSON files using [Java JSON Path](https://github.com/json-path/JsonPath)                          |
| JsoupTool             | Processing HTML files using [Jsoup](https://jsoup.org)                                                    |
| PropertiesTool        | Process JDK properties files                                                                              |
| SystemTool            | System-related utility methods                                                                            |
| XmlTool               | Process XML files using [Apache FreeMarker](https://freemarker.apache.org/docs/xgui.html)                 |
| YamlTool              | Process YAML files using [SnakeYAML](https://bitbucket.org/asomov/snakeyaml/wiki/Home)                    |
| UUIDTool              | Create UUIDs                                                                                              |


