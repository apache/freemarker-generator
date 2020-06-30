# Apache FreeMarker CLI Tools

## Overview 

The implementation of the `Apache FreeMarker CLI Tools` was inspired by [Apache Velocity Tools](https://velocity.apache.org/tools/devel/) - a `tool` is just a POJO (plain old Java object) that is "useful" in a template and is not meant to be rendered in the output.

Let's have a look at the anatomy and life-cycle of a `Apache FreeMarker CLI Tool`

* The meta-data, e.g. class name, is read from `freemarker-cli.properties`
* It provides a default constructor
* Its `toString` methods prints a short description
* It exposes public methods being used directly by the template
* It is instantiated once and is multi-thread-safe

## Available Tools

The following `tools` are currently implemented

| Tool                  | Description                                                                                               |
|-----------------------|-----------------------------------------------------------------------------------------------------------|
| CSVTool               | Process CSV files using [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/)              |
| DataFrameTool         | Bridge to [nRo/DataFrame](https://github.com/nRo/DataFrame)                                               |
| ExecTool              | Execute command line tools using [Apache Commons Exec](https://commons.apache.org/proper/commons-exec/)   |
| ExcelTool             | Process Excels files (XLS, XLSX) using [Apache POI](https://poi.apache.org)                               |
| FreeMarkerTool        | Expose useful FreeMarker classes                                                                          |
| GrokTool              | Process text files using [Grok](https://github.com/thekrakken/java-grok) instead of regular expressions   |
| GsonTool              | Process JSON files using [GSON](https://github.com/google/gson)                                           |
| JsonPathTool          | Process JSON file using [Java JSON Path](https://github.com/json-path/JsonPath)                           |
| JsoupTool             | Processing HTML files using [Jsoup](https://jsoup.org)                                                    |
| PropertiesTool        | Process JDK properties files                                                                              |
| SystemTool            | System-related utility methods                                                                            |
| UUIDTool              | Create UUIDs                                                                                              |
| XmlTool               | Process XML files using [Apache FreeMarker](https://freemarker.apache.org/docs/xgui.html)                 |
| YamlTool              | Process YAML files using [SnakeYAML](https://bitbucket.org/asomov/snakeyaml/wiki/Home)                    |

## Advanced Topics

### Auto-closing Resources

The user can create objects which need to be closed later on to avoid excessive resource usage. This is less of a concern for a short-lived CLI application but if many data sources are processed or the code is used in a different context the problem becomes more severe. 

The `Excel Tool` provides the following code to keep track of `Closables` 

```java
package org.apache.freemarker.generator.tools.excel;

public class ExcelTool {

    public Workbook parse(DataSource dataSource) {
        try (InputStream is = dataSource.getUnsafeInputStream()) {
            final Workbook workbook = WorkbookFactory.create(is);
            // make sure that the workbook is closed together with the data source
            return dataSource.addClosable(workbook);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel data source: " + dataSource, e);
        }
    }
}
```

So what is happening here 

* The `Workbook` is tracked by the originating `DataSource`
* The `DataSource` implements the `Closable` interface
* All `DataSources` are closed automatically when rendering is done.

