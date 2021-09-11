## DataSources

A `DataSource` consists of lazy-loaded data available in Apache FreeMarker's model - it provides

* A `name` uniquely identifying a data source
* An `uri` which as used to create the data source
* A `content type` and `charset`
* Access to textual content directly or using a line iterator
* Access to the underlying data input stream

### Loading A DataSource

A `DataSource` can be loaded from the file system, e.g. as positional command line argument

```
freemarker-generator -t freemarker-generator/info.ftl README.md

FreeMarker Generator DataSources
------------------------------------------------------------------------------
[#1], name=README.md, group=default, contentType=text/markdown, charset=UTF-8, length=57,188 Bytes
URI : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/README.md
```
 
from an URL

```
freemarker-generator --data-source xkcd=https://xkcd.com/info.0.json -t freemarker-generator/info.ftl

FreeMarker Generator DataSources
------------------------------------------------------------------------------
[#1]: name=xkcd, group=default, fileName=xkcd mimeType=application/json, charset=UTF-8, length=-1 Bytes
URI : https://xkcd.com/info.0.json
```

or from an environment variable, e.g. `NGINX_CONF` having a JSON payload

```
export NGINX_CONF='{"NGINX_PORT":"8443","NGINX_HOSTNAME":"localhost"}'
freemarker-generator -t freemarker-generator/info.ftl -s conf=env:///NGINX_CONF#mimeType=application/json

FreeMarker Generator DataSources
------------------------------------------------------------------------------
[#1]: name=conf, group=default, fileName=conf mimeType=application/json, charset=UTF-8, length=50 Bytes
URI : env:///NGINX_CONF
```

Of course you can load multiple `DataSources` directly

```
freemarker-generator -t freemarker-generator/info.ftl README.md xkcd=https://xkcd.com/info.0.json
 
FreeMarker Generator DataSources
------------------------------------------------------------------------------
[#1]: name=file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/README.md, group=default, fileName=README.md mimeType=text/markdown, charset=UTF-8, length=6,802 Bytes
URI : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/README.md
[#2]: name=xkcd, group=default, fileName=xkcd mimeType=application/json, charset=UTF-8, length=-1 Bytes
URI : https://xkcd.com/info.0.json
```

or load them from a directory

```
freemarker-generator -t freemarker-generator/info.ftl -s examples/data
FreeMarker Generator DataSources
------------------------------------------------------------------------------
[#1]: name=file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/accesslog/combined-access.log, group=default, fileName=combined-access.log mimeType=text/plain, charset=UTF-8, length=2,068 Bytes
URI : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/accesslog/combined-access.log
...
[#25]: name=file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/yaml/swagger-spec.yaml, group=default, fileName=swagger-spec.yaml mimeType=text/yaml, charset=UTF-8, length=17,555 Bytes
URI : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/yaml/swagger-spec.yaml
```

which can be combined with `include` and `exclude` filters

```
freemarker-generator -t freemarker-generator/info.ftl -s examples/data --data-source-include='*.json' 

FreeMarker Generator DataSources
------------------------------------------------------------------------------
[#1]: name=file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/json/github-users.json, group=default, fileName=github-users.json mimeType=application/json, charset=UTF-8, length=7,168 Bytes
URI : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/json/github-users.json
[#2]: name=file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/json/swagger-spec.json, group=default, fileName=swagger-spec.json mimeType=application/json, charset=UTF-8, length=24,948 Bytes
URI : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/json/swagger-spec.json```
```

Access to `stdin` is implemented as `DataSource` - please note that `stdin` is read lazily to cater for arbitrary large input data

```
cat examples/data/csv/contract.csv | bin/freemarker-generator -t freemarker-generator/info.ftl --stdin

FreeMarker Generator DataSources
------------------------------------------------------------------------------
[#1]: name=stdin, group=default, fileName=stdin mimeType=text/plain, charset=UTF-8, length=-1 Bytes
URI : system:///stdin
```

### Selecting A DataSource

After loading one or more `DataSource` they are accessible as `dataSource` map in the FreeMarker model

* `dataSources?values[0]` or `dataSources?values?first` selects the first data source
* `dataSources["user.csv"]` selects the data source with the name "user.csv"

### Iterating Over DataSources

The data sources are exposed as map within FreeMarker's data model 

```
<#-- Do something with the data sources -->
<#if dataSources?has_content>
Some data sources found
<#else>
No data sources found ...
</#if>

<#-- Get the number of data sources -->
${dataSources?size}

<#-- Iterate over a map of data sources -->
<#list dataSources as name, dataSource>
- ${name} => ${dataSource.length}
</#list>

<#-- Iterate over a list of data sources -->
<#list dataSources?values as dataSource>
- [#${dataSource?counter}]: name=${dataSource.name}
</#list>
```

### Filtering of DataSources

Combining FreeMarker's `filter` built-in  with the `DataSource#match` methods allows more advanced 
selection of data sources (using Apache Commons IO wild-card matching)

```
<#-- List all data sources containing "test" in the name -->
<#list dataSources?values?filter(ds -> ds.match("name", "*test*")) as ds>
- ${ds.name}
</#list>

<#-- List all data sources having "json" extension -->
<#list dataSources?values?filter(ds -> ds.match("extension", "json")) as ds>
- ${ds.name}
</#list>

<#-- List all data sources having "src/test/data/properties" in their file path -->
<#list dataSources?values?filter(ds -> ds.match("filePath", "*/src/test/data/properties")) as ds>
- ${ds.name}
</#list>

<#-- List all data sources of a group -->
<#list dataSources?values?filter(ds -> ds.match("group", "default")) as ds>
- ${ds.name}
</#list>

```

### Using a DataSource

In most cases the data source will be passed to a tool, but there are some useful operations available as shown below

```text
Invoke Arbitrary Methods On DataSource
---------------------------------------------------------------------------
<#if dataSources?has_content>
<#assign dataSource=dataSources?values?first>
Name            : ${dataSource.name}
Nr of lines     : ${dataSource.lines?size}
Content Type    : ${dataSource.contentType}
Charset         : ${dataSource.charset}
Extension       : ${dataSource.extension}
Nr of chars     : ${dataSource.text?length}
Nr of bytes     : ${dataSource.bytes?size}
File name       : ${dataSource.fileName}

Iterating Over Metadata Of A Datasource
---------------------------------------------------------------------------
<#list dataSource.metadata as name, value>
${name?right_pad(15)} : ${value}
</#list>
</#if>
```

will result in

```text
Invoke Arbitrary Methods On DataSource
---------------------------------------------------------------------------
Name            : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/src/app/examples/data/csv/contract.csv
Nr of lines     : 23
Content Type    : text/csv
Charset         : UTF-8
Extension       : csv
Nr of chars     : 6,328
Nr of bytes     : 6,328
File name       : contract.csv

Iterating Over Metadata Of A Datasource
---------------------------------------------------------------------------
extension       : csv
filename        : contract.csv
basename        : contract
filepath        : /Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/src/app/examples/data/csv
name            : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/src/app/examples/data/csv/contract.csv
mimetype        : text/csv
uri             : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/src/app/examples/data/csv/contract.csv
group           : default
```

### Inspecting A DataSource

```
> freemarker-generator \
    -t examples/templates/datasources.ftl \
    transactions:csv=examples/data/csv/transactions.csv#delimiter=TAB \
    https://xkcd.com/info.0.json \
    envvars=env:///

transactions
==============================================================================

Invoke Arbitrary Methods On DataSource
---------------------------------------------------------------------------
Name            : transactions
Group           : csv
Nr of lines     : 101
Content Type    : text/csv
Charset         : UTF-8
Extension       : csv
Nr of chars     : 12,643
Nr of bytes     : 12,643
File name       : transactions.csv
URI schema      : file

Iterating Over Metadata Of A Datasource
---------------------------------------------------------------------------
extension       : csv
basename        : transactions
filename        : transactions.csv
filepath        : /Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/csv
name            : transactions
mimetype        : text/csv
uri             : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/csv/transactions.csv
group           : csv

Iterating Over Properties Of A Datasource
---------------------------------------------------------------------------
delimiter       : TAB

https://xkcd.com/info.0.json
==============================================================================

Invoke Arbitrary Methods On DataSource
---------------------------------------------------------------------------
Name            : https://xkcd.com/info.0.json
Group           : default
Nr of lines     : 1
Content Type    : application/json
Charset         : UTF-8
Extension       :
Nr of chars     : 330
Nr of bytes     : 330
File name       :
URI schema      : https

Iterating Over Metadata Of A Datasource
---------------------------------------------------------------------------
extension       :
basename        :
filename        :
filepath        : /
name            : https://xkcd.com/info.0.json
mimetype        : application/json
uri             : https://xkcd.com/info.0.json
group           : default

Iterating Over Properties Of A Datasource
---------------------------------------------------------------------------

envvars
==============================================================================

Invoke Arbitrary Methods On DataSource
---------------------------------------------------------------------------
Name            : envvars
Group           : default
Nr of lines     : 36
Content Type    : text/plain
Charset         : UTF-8
Extension       :
Nr of chars     : 1,476
Nr of bytes     : 1,478
File name       :
URI schema      : env

Iterating Over Metadata Of A Datasource
---------------------------------------------------------------------------
extension       :
basename        :
filename        :
filepath        : /
name            : envvars
mimetype        : text/plain
uri             : env:///
group           : default

Iterating Over Properties Of A Datasource
---------------------------------------------------------------------------


```


