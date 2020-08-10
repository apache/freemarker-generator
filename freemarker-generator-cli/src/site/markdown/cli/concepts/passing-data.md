## Passing Configuration Data

`Apache FreeMarker Generator CLI` provides multiple ways to pass configuration data used in in templates

* System properties
* Parameters 

### User-Supplied System Poperties

User-supplied system properties are added to the JVM's system properties

```
> freemarker-generator -Dfoo1=foo1 -D foo2=foo2 -t info.ftl 
```
 
### User-Supplied Parameters

User-supplied parameters allow to pass additional information to an Apache FreeMarker template 

* `-Pname=value` to define a key/value pair
* `-Pname:group=value` to define a map containing key/value pairs, i.e nested map 

Pass a simple name/value pair on the command line 

```
> freemarker-generator -t info.ftl -P key=value

User Supplied Parameters
------------------------------------------------------------------------------
- key ==> value
```

By providing a `group` you can create nested maps

```
> freemarker-generator -t info.ftl -P foo1:group=bar1 -P foo2:group=bar2

User Supplied Parameters
------------------------------------------------------------------------------
- group ==> { foo1=bar1 foo2=bar2 }
```

It is also possible to mix and match the two approaches

```
> freemarker-generator -t info.ftl -P foo1:group=bar1 -P foo2:group=bar2 -P key=value

User Supplied Parameters
------------------------------------------------------------------------------
- key ==> value
- group ==> { foo1=bar1 foo2=bar2 }
```