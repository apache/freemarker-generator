## CLI Configuration
 
The `Apache FreeMarker CLI` configuration can be tweaked to

* Change the underlying `Apache FreeMarker Configuration`
* Instantiate custom tools
* Storing user-specific templates 

### Apache FreeMarker CLI Properties File

`Apache FreeMarker CLI` reads the `conf/freemarker-cli.properties`

```
#############################################################################
# General FreeMarker Configuration
# See https://freemarker.apache.org/docs/api/freemarker/template/Configuration.html#setSetting-java.lang.String-java.lang.String-
#############################################################################
# freemarker.configuration.setting.locale=JVM default

#############################################################################
# Configure FreeMarker Tools (name -> implementation class)
#############################################################################
freemarker.tools.csv=org.apache.freemarker.generator.tools.commonscsv.CommonsCSVTool
freemarker.tools.dataframe=org.apache.freemarker.generator.tools.dataframe.DataFrameTool
freemarker.tools.excel=org.apache.freemarker.generator.tools.excel.ExcelTool
freemarker.tools.exec=org.apache.freemarker.generator.tools.commonsexec.CommonsExecTool
freemarker.tools.freemarker=org.apache.freemarker.generator.tools.freemarker.FreeMarkerTool
freemarker.tools.grok=org.apache.freemarker.generator.tools.grok.GrokTool
freemarker.tools.gson=org.apache.freemarker.generator.tools.gson.GsonTool
freemarker.tools.jsonpath=org.apache.freemarker.generator.tools.json.JsonPathTool
freemarker.tools.jsoup=org.apache.freemarker.generator.tools.jsoup.JsoupTool
freemarker.tools.properties=org.apache.freemarker.generator.tools.properties.PropertiesTool
freemarker.tools.system=org.apache.freemarker.generator.tools.system.SystemTool
freemarker.tools.uuid=org.apache.freemarker.generator.tools.uuid.UUIDTool
freemarker.tools.xml=org.apache.freemarker.generator.tools.xml.XmlTool
freemarker.tools.yaml=org.apache.freemarker.generator.tools.snakeyaml.SnakeYamlTool
```

Changing this file allows to tweak the underlying `Apache FreeMarker Configuration` and add custom tools.

### Storing User-Specific Templates

Over the time you will accumulate more and more `Apache FreeMarker` templates - some of them are stored within a project but some of the more general might be free-floating and you don't want to store them in the installation directory.

To give those free-floating templates a home `Apache FreeMarker CLI` tries to read templates from `~/freemarker-cli`, e.g.

```
tree ~/.freemarker-cli/
/Users/sgoeschl/.freemarker-cli/
`-- templates
    `-- json
        |-- confluence
        |   |-- aws
        |   |   `-- describe.ftl
        |-- csv
        |   `-- swagger-endpoints.ftl
        |-- ftl
        |   `-- customer-user-products.ftl
        |-- html
        |   `-- customer-user-products.ftl
        `-- md
            `-- customer-user-products.ftl
```

If a  `~/freemarker-cli` is found it will be automatically added to the `FreeMarker Template Loader` (for more information see https://freemarker.apache.org/docs/pgui_config_templateloading.html)

You can easily check this, e.g.  

```
> freemarker-cli -t templates/info.ftl

FreeMarker CLI Template Loader Directories
------------------------------------------------------------------------------
[#1] /Users/sgoeschl
[#2] /Users/sgoeschl/.freemarker-cli
[#3] /Applications/Java/freemarker-cli-2.0.0
``` 
