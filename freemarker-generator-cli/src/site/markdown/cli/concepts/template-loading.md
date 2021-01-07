## Template Loading

In order the render a template it needs to be loaded first - there are multiple ways of creating/loading a template

* Using a [FreeMarker Template Loader](https://freemarker.apache.org/docs/pgui_config_templateloading.html) and abstract template paths 
* Load a template without template loader (aka free-style template loading), e.g. absolute template file, a directory or an URL
* Provide the template directly on the command-line (aka interactive template)

### FreeMarker MultiTemplateLoader

`Apache FreeMarker Generator CLI` uses a `MultiTemplateLoader` searching for templates in the following directories

* Current working directory
* Optional `~/.freemarker-generator` directory
* `Apache FreeMarker Generator` installation directory

You can check the currently used template loader directories easily on the command line, e.g.

```
freemarker-generator -t freemarker-generator/info.ftl

FreeMarker Generator Template Loader Directories
------------------------------------------------------------------------------
[#1] /Users/sgoeschl/work/github/apache/freemarker-generator
[#2] /Users/sgoeschl/.freemarker-generator
[#3] /Applications/Java/freemarker-generator-2.0.0
```

The main benefit of `MultiTemplateLoader` is the use of abstract template paths finding a template in the template loader directories

```
freemarker-generator -t freemarker-generator/info.ftl
``` 

and [Template Includes](https://freemarker.apache.org/docs/ref_directive_include.html)

```
<#import "/lib/commons-csv.ftl" as csv />
```  

### Free-Style Template Loading

The previously described `Template Loaders` do not support absolute template files or arbitrary URLS - this behaviour 
stems from security aspects when running `Apache FreeMarker` on the server side. For a command-line tool this is mostly
irrelevant therefore any template file outside of the template loader directories can be loaded 

This example loads the `info.ftl` directly from a GitHub URL

```
freemarker-generator -t https://raw.githubusercontent.com/apache/freemarker-generator/master/freemarker-generator-cli/src/app/templates/freemarker-generator/info.ftl
```

### Interactive Template Loading

The template can be defined directly on the command line in case of trivial transformations

```
freemarker-generator -i '${tools.gson.toJson(yaml)}' -m yaml=examples/data/yaml/swagger-spec.yaml
```

