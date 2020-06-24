## Template Loading

`Apache FreeMarker CLI` loads templates from multiple locations in the following order

* Current working directory
* Optional `~/.freemarker-cli` directory
* `Apache FreeMarker CLI` installation directory

You can check the available template locations easily on the command line

```
freemarker-cli -t templates/info.ftl

FreeMarker CLI Template Directories
------------------------------------------------------------------------------
[#1] /Users/sgoeschl/work/github/apache/freemarker-generator
[#2] /Users/sgoeschl/.freemarker-cli
[#3] /Applications/Java/freemarker-cli-2.0.0
```

### Template Loaders In Action

Let's assume you have `freemarker-cli` in your path and you execute

```
> cd /
> which freemarker-cli
/Applications/Java/freemarker-cli-2.0.0/bin/freemarker-cli
> freemarker-cli -t templates/cat.ftl https://jsonplaceholder.typicode.com/posts/2
{
  "userId": 1,
  "id": 2,
  "title": "qui est esse",
  "body": "est rerum tempore vitae\nsequi sint nihil reprehenderit dolor beatae ea dolores neque\nfugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\nqui aperiam non debitis possimus qui neque nisi nulla"
}
```

In your root directory there is no `templates/cat.ftl` but it is picked up from `/Applications/Java/freemarker-cli-2.0.0`

### Literal Templates

You can also directly specify a template to be loaded without relying on template loaders

```
freemarker-cli -t some-directory/transform.ftl some-directory/contract.csv
```

