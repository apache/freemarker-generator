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

> freemarker-cli -V
version=0.1.0-SNAPSHOT, time=2020-06-25T21:48:02+0200, commit=b320d00094be8789086ad6153d9d3fcaf4b8c75f

> freemarker-cli -h
Usage: freemarker-cli (-t=<templates> [-t=<templates>]... |
                      -i=<interactiveTemplate>) [-hV] [--stdin] [-b=<baseDir>]
                      [--config=<configFile>] [--data-source-exclude=<exclude>]
                      [--data-source-include=<include>] [-e=<inputEncoding>]
                      [-l=<locale>] [-o=<outputFile>]
                      [--output-encoding=<outputEncoding>] [--times=<times>]
                      [-D=<String=String>]... [-m=<dataModels>]...
                      [-P=<String=String>]... [-s=<dataSources>]...
                      [<sources>...]
Apache FreeMarker CLI
      [<sources>...]        data source files and/or directories
  -b, --basedir=<baseDir>   optional template base directory
      --config=<configFile> FreeMarker CLI configuration file
  -D, --system-property=<String=String>
                            set system property
      --data-source-exclude=<exclude>
                            file exclude pattern for data sources
      --data-source-include=<include>
                            file include pattern for data sources
  -e, --input-encoding=<inputEncoding>
                            encoding of data source
  -h, --help                Show this help message and exit.
  -i, --interactive=<interactiveTemplate>
                            interactive template to process
  -l, --locale=<locale>     locale being used for the output, e.g. 'en_US'
  -m, --data-model=<dataModels>
                            data model used for rendering
  -o, --output=<outputFile> output file or directory
      --output-encoding=<outputEncoding>
                            encoding of output, e.g. UTF-8
  -P, --param=<String=String>
                            set parameter
  -s, --data-source=<dataSources>
                            data source used for redering
      --stdin               read data source from stdin
  -t, --template=<templates>
                            template to process
      --times=<times>       re-run X times for profiling
  -V, --version             Print version information and exit.
```


