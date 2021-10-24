## DataModels

A `DataModel` is an eagerly loaded `DataSource` available in Apache FreeMarker's model (context) when rendering a template.

* The content of the `DataSource` is parsed and a `Map` generated
* The `Map` is either stored as variable in the model or all entries are copied into the FreeMarker model
* The parsing is supported for  `JSON`, `YAML`, `Properties` and environment variables  

Expose the fields of the JSON data source in FreeMarker's model 

```
> curl -s https://xkcd.com/info.0.json | python -m json.tool
{
    "alt": "The git vehicle fleet eventually pivoted to selling ice cream, but some holdovers remain. If you flag down an ice cream truck and hand the driver a floppy disk, a few hours later you'll get an invite to a git repo.",
    "day": "24",
    "img": "https://imgs.xkcd.com/comics/old_days_2.png",
    "link": "",
    "month": "6",
    "news": "",
    "num": 2324,
    "safe_title": "Old Days 2",
    "title": "Old Days 2",
    "transcript": "",
    "year": "2020"
}

> freemarker-generator --data-model https://xkcd.com/info.0.json  -i '<a href="${img}">${title}</a>'; echo
<a href="https://imgs.xkcd.com/comics/old_days_2.png">Old Days 2</a>
```

Exposed the JSON data source as variable `post` in FreeMarker's model 

```
> curl -s https://jsonplaceholder.typicode.com/posts/2 | python -m json.tool
{
    "body": "est rerum tempore vitae\nsequi sint nihil reprehenderit dolor beatae ea dolores neque\nfugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\nqui aperiam non debitis possimus qui neque nisi nulla",
    "id": 2,
    "title": "qui est esse",
    "userId": 1
}

> freemarker-generator --data-model post=https://jsonplaceholder.typicode.com/posts/2 -i 'post title is: ${post.title}'; echo
post title is: qui est esse
```

Expose all environment variables as `env` in the FreeMarker model
 
```
> freemarker-generator --data-model env=env:/// -i '<#list env as name,value>${name}=${value}${"\n"}</#list>'
HOME=/Users/sgoeschl
USER=sgoeschl
```

Expose a single environment variable in theFreeMarker model

```
> freemarker-generator --data-model NAME=env:///USER -i 'Hello ${NAME}'; echo
Hello sgoeschl
```

Alternatively use the short command line options, e.g.

```
> freemarker-generator -m NAME=env:///USER -i 'Hello ${NAME}!'; echo
Hello sgoeschl!
```

The following snippet shows a more advanced example

* The environment variable `DB_CONFIG` holds JSON data
* Use the `config=env:///DB_CONFIG#mimetType=application/json` to parse JSON payload from `DB_CONFIG` into the data model `config`

```
> export DB_CONFIG='{"db_default_user":"scott","db_default_password":"tiger"}'
> echo $DB_CONFIG 
{"db_default_user":"scott","db_default_password":"tiger"}
> freemarker-generator -m config=env:///DB_CONFIG#mimeType=application/json  -i '<#list config as name,value>${name}=${value}${"\n"}</#list>'
db_default_user=scott
db_default_password=tiger
```