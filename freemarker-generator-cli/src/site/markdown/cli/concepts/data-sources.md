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

Access to `stdin` is implemented as `DataSource` - please not that `stdin` is read lazily to cater for arbitrary large input data

```
cat examples/data/csv/contract.csv | bin/freemarker-generator -t freemarker-generator/info.ftl --stdin

FreeMarker Generator DataSources
------------------------------------------------------------------------------
[#1]: name=stdin, group=default, fileName=stdin mimeType=text/plain, charset=UTF-8, length=-1 Bytes
URI : system:///stdin
```

### Selecting A DataSource

After loading one or more `DataSource` they are accessible as `dataSource` map in the FreeMarker model

* `dataSources?values[0]` selects the first data source
* `dataSources["user.csv"]` selects the data source with the name "user.csv"

### Iterating Over DataSources

The data sources are exposed as map within FreeMarker's data model 

```
<#-- Do something with the data sources -->
<#if dataSources?has_content>
${dataSources?values[0].name}
<#else>
No data sources found ...
</#if>

<#-- Get the number of data sources -->
${dataSources?size}

<#-- Iterate over a map of data sources -->
<#list dataSources as name, ds>
- ${name} => ${ds.length}
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
