Apache FreeMarker Generator CLI
=============================================================================

This module provides provides the CLI for `Apache FreeMarker`.

* Requires JDK 1.8+ on Linux, Mac OSX and Windows
* Add the bin/freemarker-cli or bin/freemarker-cli.bat to your PATH variable

Now you can have a look at the command line options

```
freemarker-cli -h
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

Check the version of the `Apache FreeMarker CLI`

```
freemarker-cli -V
version=0.1.0-SNAPSHOT, time=2020-06-25T21:48:02+0200, commit=b320d00094be8789086ad6153d9d3fcaf4b8c75f
```

Or run the examples 

```
./run-examples.sh 
templates/info.ftl
examples/templates/demo.ftl
templates/csv/html/transform.ftl
templates/csv/md/transform.ftl
examples/templates/csv/shell/curl.ftl
examples/templates/csv/md/filter.ftl
examples/templates/csv/fo/transform.ftl
fop -fo target/out/locker-test-users.fo target/out/locker-test-users.pdf
examples/templates/csv/fo/transactions.ftl
fop -fo target/out/transactions.fo target/out/transactions-fo.pdf
templates/csv/html/transform.ftl
wkhtmltopdf -O landscape target/out/transactions.html target/out/transactions-html.pdf
examples/templates/dataframe/example.ftl
examples/templates/accesslog/combined-access.ftl
examples/templates/excel/dataframe/transform.ftl
templates/excel/html/transform.ftl
templates/excel/md/transform.ftl
templates/excel/csv/transform.ftl
examples/templates/excel/csv/custom.ftl
examples/templates/html/csv/dependencies.ftl
examples/templates/json/csv/swagger-endpoints.ftl
templates/json/yaml/transform.ftl
examples/templates/json/md/github-users.ftl
examples/templates/properties/csv/locker-test-users.ftl
examples/data/template
examples/templates/yaml/txt/transform.ftl
templates/yaml/json/transform.ftl
examples/templates/xml/txt/recipients.ftl
Created the following sample files in ./target/out
total 1464
-rw-r--r--  1 sgoeschl  staff     646 Jun 28 08:21 combined-access.log.txt
-rw-r--r--  1 sgoeschl  staff   25676 Jun 28 08:20 contract.html
-rw-r--r--  1 sgoeschl  staff    7933 Jun 28 08:20 contract.md
-rw-r--r--  1 sgoeschl  staff     784 Jun 28 08:20 curl.sh
-rw-r--r--  1 sgoeschl  staff     232 Jun 28 08:21 customer.txt
-rw-r--r--  1 sgoeschl  staff    6486 Jun 28 08:21 dataframe.txt
-rw-r--r--  1 sgoeschl  staff   15563 Jun 28 08:20 demo.txt
-rw-r--r--  1 sgoeschl  staff    1310 Jun 28 08:21 dependencies.csv
-rw-r--r--  1 sgoeschl  staff    2029 Jun 28 08:21 github-users-curl.md
-rw-r--r--  1 sgoeschl  staff    2624 Jun 28 08:20 info.txt
-rw-r--r--  1 sgoeschl  staff    8075 Jun 28 08:20 interactive-dataframe.txt
-rw-r--r--  1 sgoeschl  staff      66 Jun 28 08:20 interactive-html.txt
-rw-r--r--  1 sgoeschl  staff      16 Jun 28 08:20 interactive-json.txt
-rw-r--r--  1 sgoeschl  staff   25090 Jun 28 08:20 interactive-swagger.json
-rw-r--r--  1 sgoeschl  staff   16870 Jun 28 08:20 interactive-swagger.yaml
-rw-r--r--  1 sgoeschl  staff      10 Jun 28 08:20 interactive-xml.txt
-rw-r--r--  1 sgoeschl  staff     285 Jun 28 08:21 locker-test-users.csv
-rw-r--r--  1 sgoeschl  staff    6341 Jun 28 08:20 locker-test-users.fo
-rw-r--r--  1 sgoeschl  staff    5526 Jun 28 08:20 locker-test-users.pdf
-rw-r--r--  1 sgoeschl  staff     921 Jun 28 08:21 recipients.txt
-rw-r--r--  1 sgoeschl  staff     910 Jun 28 08:20 sales-records.md
-rw-r--r--  1 sgoeschl  staff    2453 Jun 28 08:21 swagger-spec.csv
-rw-r--r--  1 sgoeschl  staff   25090 Jun 28 08:21 swagger-spec.json
-rw-r--r--  1 sgoeschl  staff   16870 Jun 28 08:21 swagger-spec.yaml
drwxr-xr-x  4 sgoeschl  staff     128 Jun 28 08:21 template
-rw-r--r--  1 sgoeschl  staff     154 Jun 28 08:21 test-multiple-sheets.xlsx.csv
-rw-r--r--  1 sgoeschl  staff    1917 Jun 28 08:21 test-multiple-sheets.xlsx.html
-rw-r--r--  1 sgoeschl  staff     389 Jun 28 08:21 test-multiple-sheets.xlsx.md
-rw-r--r--  1 sgoeschl  staff     155 Jun 28 08:21 test-transform-xls.csv
-rw-r--r--  1 sgoeschl  staff    1439 Jun 28 08:21 test.xls.dataframe.txt
-rw-r--r--  1 sgoeschl  staff    1556 Jun 28 08:21 test.xls.html
-rw-r--r--  1 sgoeschl  staff    1558 Jun 28 08:21 test.xslx.html
-rw-r--r--  1 sgoeschl  staff   25758 Jun 28 08:20 transactions-fo.pdf
-rw-r--r--  1 sgoeschl  staff   66016 Jun 28 08:21 transactions-html.pdf
-rw-r--r--  1 sgoeschl  staff  330128 Jun 28 08:20 transactions.fo
-rw-r--r--  1 sgoeschl  staff   51008 Jun 28 08:21 transactions.html
```


