## Named URIs

Named URIs allow to identify `DataSources` and pass additional information 

A Named URI consists of

* an optional name
* an URI or simple file name

As a refresher, a URI is made up of the following components (inspired by https://docs.gomplate.ca/datasources/)

```
  foo://userinfo@example.com:8042/over/there?name=ferret#nose
  \_/   \_______________________/\_________/ \_________/ \__/
   |           |                    |            |        |
scheme     authority               path        query   fragment
```

For our purposes, the scheme and the path components are especially important, though the other components are used by certain datasources for particular purposes.

| Component | Purpose                                                                                                   |
|-----------|-----------------------------------------------------------------------------------------------------------|
| scheme	| All datasources require a scheme (except for file when using relative paths)                              |
| authority	| Used only by remote datasources, and can be omitted in some of those cases.                               |
| path	    | Can be omitted, but usually used as the basis of the locator for the datasource.                          |
| query	    | Used mainly for HTTP and HTTPS URLs                                                                       |
| fragment	| Used rarely for providing additional attributes, e.g. `mimetype` of `charset`                             |

The following Named URI loads a "user.csv" and the data source is available as `my_users` 

```
bin/freemarker-cli -t templates/info.ftl my_users=examples/data/csv/user.csv
[#1], name=my_users, group=default, contentType=text/csv, charset=UTF-8, length=376 Bytes
URI : file:examples/data/csv/user.csv
```

A Named URI allows to pass additional information as part of the fragment, e.g. the charset of the text file 

```
bin/freemarker-cli -t templates/info.ftl my_users=examples/data/csv/user.csv#charset=UTF-16
[#1], name=my_users, group=default, contentType=text/csv, charset=UTF-16, length=376 Bytes
URI : file:examples/data/csv/user.csv
```

In addition to the simplified file syntax full URIs can be used

```
bin/freemarker-cli -t templates/info.ftl http://google.com?foo=bar
[#1], name=google.com, group=default, contentType=text/html, charset=ISO-8859-1, length=-1 Bytes
URI : http://google.com?foo=bar
```

and also combined with a name

```
bin/freemarker-cli -t templates/info.ftl page=http://google.com?foo=bar
[#1], name=page, group=default, contentType=text/html, charset=ISO-8859-1, length=-1 Bytes
URI : http://google.com?foo=bar
```
