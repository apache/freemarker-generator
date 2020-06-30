## Design Goals

* Create a proper command-line tool which has Unix look & feel
* Handle arbitrary large input and output data
* Support multiple source files/directories for a single transformation
* Support transformation of Property files using plain-vanilla JDK
* Support transformation of CSV files using [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/)
* Support transformation of JSON using [Jayway's JSONPath](https://github.com/jayway/JsonPath) and [GSON](https://github.com/google/gson)
* Support transformation of Excel using [Apache POI](https://poi.apache.org)
* Support transformation of YAML using [SnakeYAML](https://bitbucket.org/asomov/snakeyaml/wiki/Home)
* Support transformation of HTML using [JSoup](https://jsoup.org)
* Support transformation of structured logfiles using [Grok](https://github.com/thekrakken/java-grok)
* XML & XPath is supported by FreeMarker [out-of-the-box](http://freemarker.org/docs/xgui.html)
* Support for reading a data source content from STDIN to integrate with command line tools
* Support execution of arbitrary commands using [Apache Commons Exec](https://commons.apache.org/proper/commons-exec/)
* Add some commonly useful information such as `System Properties`, `Enviroment Variables`
* Support embedding the code in existing applications