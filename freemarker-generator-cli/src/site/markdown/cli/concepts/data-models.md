## DataModels

A `DataModel` is an eagerly loaded `DataSource` available in Apache FreeMarker's model (context) when rendering a template.

* The content of the `DataSource` is parsed and a `Map` generated
* The `Map` is either stored as variable in the model or all entries are copied into the FreeMarker model
* The parsing is supported for  `JSON`, `YAML`, `Properties` and enviroment variables  

Expose the fields of the JSON data source in FreeMarker's model 

```
bin/freemarker-cli --data-model https://xkcd.com/info.0.json  -i '<a href="${img}">${title}</a>'; echo
<a href="https://imgs.xkcd.com/comics/scenario_4.png">Scenario 4</a>
```

Exposed the JSON data source as variable `post` in FreeMarker's model 

```
bin/freemarker-cli --data-model post=https://jsonplaceholder.typicode.com/posts/2 -i 'post title is: ${post.title}'; echo
post title is: qui est esse
```

Expose all environment variables as `env` in theFreeMarker model
 
```
bin/freemarker-cli --data-model env=env:/// -i '<#list env as name,value>${name}=${value}${"\n"}</#list>'
HOME=/Users/sgoeschl
USER=sgoeschl
```

Expose a single envionment variable in theFreeMarker model

```
bin/freemarker-cli --data-model NAME=env:///USER -i 'Hello ${NAME}'; echo
Hello sgoeschl
```

Alternatively use the short command line options, e.g.

```
bin/freemarker-cli -m NAME=env:///USER -i 'Hello ${NAME}!'; echo
Hello sgoeschl!
```

The following snippet shows a more advanced example

* The environment variable `DB_CONFIG` holds JSON data
* Use the `config=env:///DB_CONFIG#mimetype=application/json` to parse JSON payload from `DB_CONFIG` into the data model `config`

```
> export DB_CONFIG='{"db_default_user":"scott","db_default_password":"tiger"}'
> echo $DB_CONFIG 
{"db_default_user":"scott","db_default_password":"tiger"}
> bin/freemarker-cli -m config=env:///DB_CONFIG#mimetype=application/json  -i '<#list config as name,value>${name}=${value}${"\n"}</#list>'
db_default_user=scott
db_default_password=tiger
```