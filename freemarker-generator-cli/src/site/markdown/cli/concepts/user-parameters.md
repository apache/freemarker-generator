# User-Supplied Parameters

User-supplied parameters allow to pass additional information to an Apache FreeMarker template 

* `-Pname=value` to define a key/value pair
* `-Pname:group=value` to define a map containing key/value pairs, i.e nested map 

Pass a simple name/value pair on the command line 

```
> bin/freemarker-cli -t templates/info.ftl -P key=value

User Supplied Parameters
------------------------------------------------------------------------------
- key ==> value
```

By providing a `group` you can create nested maps

```
> bin/freemarker-cli -t templates/info.ftl -P foo1:group=bar1 -P foo2:group=bar2

User Supplied Parameters
------------------------------------------------------------------------------
- group ==> { foo1=bar1 foo2=bar2 }
```

It is also possible to mix and match the two approaches

```
> bin/freemarker-cli -t templates/info.ftl -P foo1:group=bar1 -P foo2:group=bar2 -P key=value

User Supplied Parameters
------------------------------------------------------------------------------
- key ==> value
- group ==> { foo1=bar1 foo2=bar2 }
```