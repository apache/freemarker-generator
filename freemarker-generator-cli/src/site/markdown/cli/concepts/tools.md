## Tools

A `Apache FreeMarker Generator Tool` is just a POJO (plain old Java object) that is "useful" in a template and is not meant to be rendered in the output.

The following tools are currently implemented

| Implementation Class  | Name          | Description                                                                                               |
|-----------------------|---------------|-----------------------------------------------------------------------------------------------------------|
| CSVTool               | csv           | Process CSV files using [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/)              |
| DataFrameTool         | dataframe     | Bridge to [nRo/DataFrame](https://github.com/nRo/DataFrame)                                               |
| ExcelTool             | excel         | Process Excels files (XLS, XLSX) using [Apache POI](https://poi.apache.org)                               |
| ExecTool              | exec          | Execute command line tools using [Apache Commons Exec](https://commons.apache.org/proper/commons-exec/)   |
| FreeMarkerTool        | freemarker    | Expose useful FreeMarker classes                                                                          |
| GrokTool              | grok          | Process text files using [Grok](https://github.com/thekrakken/java-grok) instead of regular expressions   |
| GsonTool              | gson          | Process JSON files using [GSON](https://github.com/google/gson)                                           |
| JavaFakerTool         | javafaker     | Generate test data using Java Faker [JavaFaker](https://github.com/DiUS/java-faker)
| JsonPathTool          | jsonpath      | Process JSON file using [Java JSON Path](https://github.com/json-path/JsonPath)                           |
| JsoupTool             | jsoup         | Processing HTML files using [Jsoup](https://jsoup.org)                                                    |
| PropertiesTool        | properties    | Process JDK properties files                                                                              |
| SystemTool            | system        | System-related utility methods                                                                            |
| UUIDTool              | uuid          | Create UUIDs                                                                                              |
| XmlTool               | xaml          | Process XML files using [Apache FreeMarker](https://freemarker.apache.org/docs/xgui.html)                 |
| YamlTool              | yaml          | Process YAML files using [SnakeYAML](https://bitbucket.org/asomov/snakeyaml/wiki/Home)                    |
