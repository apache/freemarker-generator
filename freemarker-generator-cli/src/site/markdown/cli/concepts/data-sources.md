## DataSources

A `DataSource` consists of lazy-loaded data available in Apache FreeMarker's model (context) - it provides

* a `charset` for reading textual content
* a `content type`
* a `name` and a `group`
* access to textual content directly or using a line iterator
* access to the data input stream

### Loading A DataSource

A `DataSource` can be loaded from the file system, e.g. as positional command line argument

```
freemarker-cli -t templates/info.ftl README.md

FreeMarker CLI DataSources
------------------------------------------------------------------------------
    [#1], name=README.md, group=default, contentType=text/markdown, charset=UTF-8, length=57,188 Bytes
    URI : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/README.md
```
 
from an URL

```
freemarker-cli --data-source xkcd=https://xkcd.com/info.0.json -t templates/info.ftl

FreeMarker CLI DataSources
------------------------------------------------------------------------------
    [#1], name=xkcd, group=default, contentType=application/json, charset=UTF-8, length=-1 Bytes
    URI : https://xkcd.com/info.0.json 
```

or from an environment variable, e.g. `NGINX_CONF` having a JSON payload

```
export NGINX_CONF='{"NGINX_PORT":"8443","NGINX_HOSTNAME":"localhost"}'
freemarker-cli -t templates/info.ftl -s conf=env:///NGINX_CONF#mimeType=application/json

FreeMarker CLI DataSources
------------------------------------------------------------------------------
    [#1], name=conf, group=default, contentType=application/json, charset=UTF-8, length=50 Bytes
    URI : env:///NGINX_CONF
```

Of course you can load multiple `DataSources` directly

```
freemarker-cli -t templates/info.ftl README.md xkcd=https://xkcd.com/info.0.json
 
FreeMarker CLI DataSources
------------------------------------------------------------------------------
    [#1], name=README.md, group=default, contentType=text/markdown, charset=UTF-8, length=57,188 Bytes
    URI : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/README.md
    [#2], name=xkcd, group=default, contentType=application/json, charset=UTF-8, length=-1 Bytes
    URI : https://xkcd.com/info.0.json
```

or load them from a directory

```
freemarker-cli -t templates/info.ftl -s examples/data
FreeMarker CLI DataSources
------------------------------------------------------------------------------
    [#1], name=combined-access.log, group=default, contentType=text/plain, charset=UTF-8, length=2,068 Bytes
    URI : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/accesslog/combined-access.log
    ...
    [#22], name=swagger-spec.yaml, group=default, contentType=text/yaml, charset=UTF-8, length=17,555 Bytes
    URI : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/yaml/swagger-spec.yaml

```

which can be combined with `include` and `exclude` filters

```
freemarker-cli -t templates/info.ftl -s examples/data --data-source-include=*.json

FreeMarker CLI DataSources
------------------------------------------------------------------------------
    [#1], name=github-users.json, group=default, contentType=application/json, charset=UTF-8, length=7,168 Bytes
    URI : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/json/github-users.json
    [#2], name=swagger-spec.json, group=default, contentType=application/json, charset=UTF-8, length=24,948 Bytes
    URI : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/json/swagger-spec.json
```

### Selecting A DataSource

After loading one or more `DataSource` it needs to be selected for template processing - the `DataSources` instance 
exposed in the data model provides

* Selecting by index  
* Selecting by name
* Filter by the globbing pattern (see [Apache Commons IO](https://commons.apache.org/proper/commons-io/javadocs/api-release/org/apache/commons/io/filefilter/WildcardFileFilter.html))

A few FTL examples

```
<#assign dataSource = dataSources.get(0)>

<#assign dataSource = dataSources.get("user.csv)>

<#list dataSources.find("*.md") as dataSource>
- ${dataSource.name}
</#list>
```



 

