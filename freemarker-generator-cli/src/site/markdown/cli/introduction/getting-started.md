## Getting Started

### Installation

* Requires JDK 1.8+ on Linux, Mac OSX and Windows
* Download [freemarker-generator-cli-0.1.0-SNAPSHOT-app.tar.gz] or [freemarker-generator-cli-0.1.0-SNAPSHOT-app.zip]
* Unpack the archive in a directory of your choice
* Add the `bin/freemarker-generator` or `bin/freemarker-generator.bat` to your `PATH` variable

### Verify Installation

On my local box (Mac OS 10.15.5) I use the following setup

```
export FREEMARKER_CLI_HOME=/Applications/Java/freemarker-generator-2.0.0
export PATH=$PATH:$FREEMARKER_CLI_HOME/bin
```

Afterwards `Apache FreeMarker Generator` can be executed from the command line

```
> which freemarker-generator
/Applications/Java/freemarker-generator-2.0.0/bin/freemarker-generator
```

and check the version of `Apache FreeMarker Generator`

```
> freemarker-generator -V
version=0.1.0-SNAPSHOT, time=2020-06-25T21:48:02+0200, commit=b320d00094be8789086ad6153d9d3fcaf4b8c75f
```

### Command Line Options

`Apache FreeMarker Generator` provides command line help as shown below

```
> freemarker-generator -h
Usage: freemarker-generator (-t=<templates> [-t=<templates>]... |
                            -i=<interactiveTemplate>) [-hV] [--stdin]
                            [--config=<configFile>]
                            [--data-source-exclude=<dataSourceExcludePattern>]
                            [--data-source-include=<dataSourceIncludePattern>]
                            [-e=<inputEncoding>] [-l=<locale>]
                            [--output-encoding=<outputEncoding>]
                            [--template-dir=<templateDir>] [--times=<times>]
                            [-D=<String=String>]... [-m=<dataModels>]...
                            [-o=<outputs>]... [-P=<String=String>]...
                            [-s=<dataSources>]... [<sources>...]
Apache FreeMarker Generator
      [<sources>...]       data source files and/or directories
      --config=<configFile>
                           FreeMarker Generator configuration file
  -D, --system-property=<String=String>
                           set system property
      --data-source-exclude=<dataSourceExcludePattern>
                           file exclude pattern for data sources
      --data-source-include=<dataSourceIncludePattern>
                           file include pattern for data sources
  -e, --input-encoding=<inputEncoding>
                           encoding of data source
  -h, --help               Show this help message and exit.
  -i, --interactive=<interactiveTemplate>
                           interactive template to process
  -l, --locale=<locale>    locale being used for the output, e.g. 'en_US'
  -m, --data-model=<dataModels>
                           data model used for rendering
  -o, --output=<outputs>   output files or directories
      --output-encoding=<outputEncoding>
                           encoding of output, e.g. UTF-8
  -P, --param=<String=String>
                           set parameter
  -s, --data-source=<dataSources>
                           data source used for rendering
      --stdin              read data source from stdin
  -t, --template=<templates>
                           templates to process
      --template-dir=<templateDir>
                           additional template directory
      --times=<times>      re-run X times for profiling
  -V, --version            Print version information and exit.
```

### The Info Template

The distribution ships with a couple of FreeMarker templates and the `templates/info.ftl` is particularly helpful 
to better understand `Apache FreeMarker Generator`

```
> freemarker-generator -t freemarker-generator/info.ftl 
FreeMarker Generator Information
------------------------------------------------------------------------------
FreeMarker version     : 2.3.30
Template name          : freemarker-generator/info.ftl
Language               : en
Locale                 : en_US
Timestamp              : Jan 7, 2021 11:36:26 PM
Output encoding        : UTF-8
Output format          : plainText

FreeMarker Generator Template Loader Directories
------------------------------------------------------------------------------
[#1] /Users/sgoeschl/.freemarker-generator/templates
[#2] /Applications/Java/freemarker-generator/templates

FreeMarker Generator Data Model
---------------------------------------------------------------------------
- dataSources

FreeMarker Generator Tools
------------------------------------------------------------------------------
- csv                  : Process CSV files using Apache Commons CSV (see https://commons.apache.org/proper/commons-csv/)
- dataframe            : Bridge to [nRo/DataFrame](https://github.com/nRo/DataFrame)
- excel                : Process Excels files (XLS, XLSX) using Apache POI (see https://poi.apache.org)
- exec                 : Execute command line tools using Apache Commons Exec (see https://commons.apache.org/proper/commons-exec/)
- freemarker           : Expose advanced Apache FreeMarker classes
- grok                 : Process text files using Grok expressions (see https://github.com/thekrakken/java-grok)
- gson                 : Process JSON files using GSON (see https://github.com/google/gson)
- javafaker            : Generate test data using Java Faker (see https://github.com/DiUS/java-faker)
- jsonpath             : Process JSON files using Java JSON Path (see https://github.com/json-path/JsonPath)
- jsoup                : Process  HTML files using Jsoup (see https://jsoup.org)
- properties           : Process JDK properties files
- system               : Expose System-related utility methods
- uuid                 : Create UUIDs
- xml                  : Process XML files using Apache FreeMarker (see https://freemarker.apache.org/docs/xgui.html)
- yaml                 : Process YAML files using SnakeYAML(see https://bitbucket.org/asomov/snakeyaml/wiki/Home)
```

* The "FreeMarker Generator Information" section provides insights into configuration and currently processed template.
* The "FreeMarker Generator Template Loader Directories" shows the template directories being searched to resolve a template path
* The "FreeMarker Generator Tools" section list the available tools
* The "FreeMarker Generator Data Model" section shows all available entries in the current FreeMarker context 
