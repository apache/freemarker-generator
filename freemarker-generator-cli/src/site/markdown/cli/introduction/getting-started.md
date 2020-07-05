## Getting Started

### Installation

* Requires JDK 1.8+ on Linux, Mac OSX and Windows
* Download [freemarker-generator-cli-0.1.0-SNAPSHOT-app.tar.gz] or [freemarker-generator-cli-0.1.0-SNAPSHOT-app.zip]
* Unpack the archive in a directory of your choice
* Add the `bin/freemarker-cli` or `bin/freemarker-cli.bat` to your `PATH` variable

### Verify Installation

On my local box (Mac OS 10.15.5) I use the following setup

```
export FREEMARKER_CLI_HOME=/Applications/Java/freemarker-cli-2.0.0
export PATH=$PATH:$FREEMARKER_CLI_HOME/bin
```

Afterwards `Apache FreeMarker CLI` can be executed from the command line

```
> which freemarker-cli
/Applications/Java/freemarker-cli-2.0.0/bin/freemarker-cli
```

and check the version of `Apache FreeMarker CLI`

```
> freemarker-cli -V
version=0.1.0-SNAPSHOT, time=2020-06-25T21:48:02+0200, commit=b320d00094be8789086ad6153d9d3fcaf4b8c75f
```

### Command Line Options

`Apache FreeMarker CLI` provides command line help as shown below

```
> freemarker-cli -h
  Usage: freemarker-cli (-t=<templates> [-t=<templates>]... |
                        -i=<interactiveTemplate>) [-hV] [--stdin] [-b=<baseDir>]
                        [--config=<configFile>]
                        [--data-source-exclude=<dataSourceExcludePattern>]
                        [--data-source-include=<dataSourceIncludePattern>]
                        [-e=<inputEncoding>] [-l=<locale>]
                        [--output-encoding=<outputEncoding>] [--times=<times>]
                        [-D=<String=String>]... [-m=<dataModels>]...
                        [-o=<outputs>]... [-P=<String=String>]...
                        [-s=<dataSources>]... [<sources>...]
  Apache FreeMarker CLI
        [<sources>...]        data source files and/or directories
    -b, --basedir=<baseDir>   additional template base directory
        --config=<configFile> FreeMarker CLI configuration file
    -D, --system-property=<String=String>
                              set system property
        --data-source-exclude=<dataSourceExcludePattern>
                              file exclude pattern for data sources
        --data-source-include=<dataSourceIncludePattern>
                              file include pattern for data sources
    -e, --input-encoding=<inputEncoding>
                              encoding of data source
    -h, --help                Show this help message and exit.
    -i, --interactive=<interactiveTemplate>
                              interactive template to process
    -l, --locale=<locale>     locale being used for the output, e.g. 'en_US'
    -m, --data-model=<dataModels>
                              data model used for rendering
    -o, --output=<outputs>    output files or directories
        --output-encoding=<outputEncoding>
                              encoding of output, e.g. UTF-8
    -P, --param=<String=String>
                              set parameter
    -s, --data-source=<dataSources>
                              data source used for rendering
        --stdin               read data source from stdin
    -t, --template=<templates>
                              templates to process
        --times=<times>       re-run X times for profiling
    -V, --version             Print version information and exit.
```

### The Info Template

The distribution ships with a couple of FreeMarker templates and the `templates/info.ftl` is particularly helpful 
to better understand `Apache FreeMarker CLI`

```
> freemarker-cli -t templates/info.ftl
FreeMarker CLI Information
------------------------------------------------------------------------------
FreeMarker version     : 2.3.30
Template name          : templates/info.ftl
Language               : en
Locale                 : en_US
Timestamp              : Jun 26, 2020 10:44:15 AM
Output encoding        : UTF-8
Output format          : plainText

FreeMarker CLI Template Loader Directories
------------------------------------------------------------------------------
[#1] /Users/sgoeschl/work/github/apache/freemarker-generator
[#2] /Users/sgoeschl/.freemarker-cli
[#3] /Applications/Java/freemarker-cli-2.0.0

FreeMarker CLI Tools
------------------------------------------------------------------------------
- CSVTool              : Process CSV files using Apache Commons CSV (see https://commons.apache.org/proper/commons-csv/)
- DataFrameTool        : Bridge to [nRo/DataFrame](https://github.com/nRo/DataFrame)
- ExcelTool            : Process Excels files (XLS, XLSX) using Apache POI (see https://poi.apache.org)
- ExecTool             : Execute command line tools using Apache Commons Exec (see https://commons.apache.org/proper/commons-exec/)
- FreeMarkerTool       : Expose advanced Apache FreeMarker classes
- GrokTool             : Process text files using Grok expressions (see https://github.com/thekrakken/java-grok)
- GsonTool             : Process JSON files using GSON (see https://github.com/google/gson)
- JsonPathTool         : Process JSON files using Java JSON Path (see https://github.com/json-path/JsonPath)
- JsoupTool            : Process  HTML files using Jsoup (see https://jsoup.org)
- PropertiesTool       : Process JDK properties files
- SystemTool           : Expose System-related utility methods
- UUIDTool             : Create UUIDs
- XmlTool              : Process XML files using Apache FreeMarker (see https://freemarker.apache.org/docs/xgui.html)
- YamlTool             : Process YAML files using SnakeYAML(see https://bitbucket.org/asomov/snakeyaml/wiki/Home)

FreeMarker CLI Data Model
---------------------------------------------------------------------------
- CSVTool
- DataFrameTool
- DataSources
- ExcelTool
- ExecTool
- FreeMarkerTool
- GrokTool
- GsonTool
- JsonPathTool
- JsoupTool
- PropertiesTool
- SystemTool
- UUIDTool
- XmlTool
- YamlTool
```

* The "FreeMarker CLI Information" section provides insights into configuration and currently processed template.
* The "FreeMarker CLI Template Loader Directories" shows the template directories being searched to resolve a template path
* The "FreeMarker CLI Tools" section list the available tools
* The "FreeMarker CLI Data Model" section shows all available entries in the current FreeMarker context 
