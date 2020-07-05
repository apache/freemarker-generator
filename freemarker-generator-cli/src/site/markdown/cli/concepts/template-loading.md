## Template Loading

In order the render a template it needs to be loaded first - there are multiple ways of creating/loading a template

* Using a [FreeMarker Template Loader](https://freemarker.apache.org/docs/pgui_config_templateloading.html) and abstract template paths 
* Load a template without template loader (aka free-style template loading), e.g. absolute template file, a directory or an URL
* Provide the template directly on the command-line (aka interactive template)

### FreeMarker MultiTemplateLoader

`Apache FreeMarker CLI` uses a `MultiTemplateLoader` searching for templates in the following directories

* Current working directory
* Optional `~/.freemarker-cli` directory
* `Apache FreeMarker CLI` installation directory

You can check the currently used template loader directories easily on the command line, e.g.

```
freemarker-cli -t templates/info.ftl

FreeMarker CLI Template Loader Directories
------------------------------------------------------------------------------
[#1] /Users/sgoeschl/work/github/apache/freemarker-generator
[#2] /Users/sgoeschl/.freemarker-cli
[#3] /Applications/Java/freemarker-cli-2.0.0
```

The main benefit of `MultiTemplateLoader` is the use of abstract template paths finding a template in the template loader directories

```
freemarker-cli -t templates/info.ftl
``` 

and [Template Includes](https://freemarker.apache.org/docs/ref_directive_include.html)

```
<#import "/templates/lib/commons-csv.ftl" as csv />
```  

### Free-Style Template Loading

The previosly described `Template Loaders` do not support absolute template files or arbitrary URLS - this behaviour 
stems from security aspects when running `Apache FreeMarker` on the server side. For a command-line tool this is mostly
irrelevant therefore any template file outside of the template loader directories can be loaded 

This example loads the `info.ftl` directly from a GitHub URL

```
freemarker-cli -t https://raw.githubusercontent.com/apache/freemarker-generator/master/freemarker-generator-cli/templates/info.ftl
```

### Interactive Template Loading

The template can be defined directly on the command line in case of trivial transformations

```
freemarker-cli -i '${GsonTool.toJson(yaml)}' -m yaml=examples/data/yaml/swagger-spec.yaml
```

