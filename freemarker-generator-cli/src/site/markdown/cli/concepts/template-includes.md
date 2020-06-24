### Template Includes

When writing more and more templates you find reusable snippets which should not be copy/pasted but organized as [templates includes](https://freemarker.apache.org/docs/ref_directive_include.html).

E.g. `Apache Commons CSV` FTL code is included as shown below

```
<#import "/templates/lib/commons-csv.ftl" as csv />
<#assign dataSource = DataSources.get(0)>
<#assign csvParser = CSVTool.parse(dataSource, csv.sourceFormat())>
```

What is happening here?

* The template loader picks up `/templates/lib/commons-csv.ftl` from the `Apache FreeMarker CLI` installation directory and imports it as `csv`
* The imported template exports two methods `csv.sourceFormat()` and `csv.targetFormat()` 
* The exposed methods do the right thing based on the user-supplied parameters and can be re-used for all CSV related templates
