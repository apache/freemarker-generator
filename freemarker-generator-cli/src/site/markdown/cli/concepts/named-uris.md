## Named URIs

Named URIs allow identifying `DataSources` and pass additional information 

A Named URI consists of

* an optional name
* an URI or simple file name

As a refresher, a URI is made up of the following components (inspired by https://docs.gomplate.ca/datasources/)

```text
  foo://userinfo@example.com:8042/over/there?name=ferret#nose
  \_/   \_______________________/\_________/ \_________/ \__/
   |           |                    |            |        |
scheme     authority               path        query   fragment
```

For our purposes, the scheme and the path components are especially important, though the other components are used by certain data sources for particular purposes.

| Component | Purpose                                                                                                   |
|-----------|-----------------------------------------------------------------------------------------------------------|
| scheme	| All data sources require a scheme (except for file when using relative paths)                             |
| authority	| Used only by remote data sources, and can be omitted in some of those cases.                              |
| path	    | Can be omitted, but usually used as the basis of the locator for the datasource.                          |
| query	    | Used mainly for HTTP and HTTPS URLs                                                                       |
| fragment	| Used rarely for providing additional attributes, e.g. `mimeType` of `charset`                             |

### Using Named URIs For A File

The following Named URI loads a "user.csv" and the data source is available as `my_users`

```text
freemarker-generator -t freemarker-generator/info.ftl my_users=examples/data/csv/user.csv

FreeMarker Generator DataSources
------------------------------------------------------------------------------
[#1]: name=my_users, group=default, fileName=my_users mimeType=text/csv, charset=UTF-8, length=376 Bytes
URI : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/csv/user.csv
```

A Named URI allows to pass additional information as part of the fragment, e.g. the charset of the text file 

```text
freemarker-generator -t freemarker-generator/info.ftl my_users=examples/data/csv/user.csv#charset=UTF-16

FreeMarker Generator DataSources
------------------------------------------------------------------------------
[#1]: name=my_users, group=default, fileName=my_users mimeType=text/csv, charset=UTF-16, length=376 Bytes
URI : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/csv/user.csv
```

In addition to the simplified file syntax full URIs can be used

```text
freemarker-generator -t freemarker-generator/info.ftl 'http://google.com?foo=bar'

FreeMarker Generator DataSources
------------------------------------------------------------------------------
[#1]: name=http://google.com?foo=bar, group=default, fileName=google.com?foo=bar mimeType=text/html, charset=ISO-8859-1, length=-1 Bytes
URI : http://google.com?foo=bar
```

and also combined with a name

```text
freemarker-generator -t freemarker-generator/info.ftl 'page=http://google.com?foo=bar'

FreeMarker Generator DataSources
------------------------------------------------------------------------------
[#1]: name=page, group=default, fileName=page mimeType=text/html, charset=ISO-8859-1, length=-1 Bytes
URI : http://google.com?foo=bar
```

### Using Named URIs For Directories

A Name URI can be also combined with file directories.

Load all CVS files of a directory using the group "csv"

```text
freemarker-generator -t freemarker-generator/info.ftl :csv=examples/data/csv

FreeMarker Generator DataSources
------------------------------------------------------------------------------
[#1]: name=file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/csv/contract.csv, group=csv, fileName=contract.csv, mimeType=text/csv, charset=UTF-8, length=6,328 Bytes
URI : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/csv/contract.csv
...
[#7]: name=file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/csv/user.csv, group=csv, fileName=user.csv, mimeType=text/csv, charset=UTF-8, length=376 Bytes
URI : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/csv/user.csv
```

or use a charset for all files of a directory

```text
freemarker-generator -t freemarker-generator/info.ftl 'examples/data/csv#charset=UTF-16&mimetype=text/plain'

FreeMarker Generator DataSources
------------------------------------------------------------------------------
[#1]: name=file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/csv/contract.csv, group=default, fileName=contract.csv, mimeType=text/csv, charset=UTF-16, length=6,328 Bytes
URI : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/csv/contract.csv
...
[#7]: name=file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/csv/user.csv, group=default, fileName=user.csv, mimeType=text/csv, charset=UTF-16, length=376 Bytes
URI : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/csv/user.csv
```

It is also possible to provide data source properties to all files being loaded

```text
freemarker-generator -t examples/templates/datasources.ftl 'examples/data/csv#format=DEFAULT'

file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/csv/user.csv
==============================================================================

Invoke Arbitrary Methods On DataSource
---------------------------------------------------------------------------
Name            : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/csv/user.csv
Group           : default
Nr of lines     : 5
Content Type    : text/csv
Charset         : UTF-8
Extension       : csv
Nr of chars     : 376
Nr of bytes     : 376
File name       : user.csv
URI schema      : file

Iterating Over Metadata Of A Datasource
---------------------------------------------------------------------------
extension       : csv
baseName        : user
fileName        : user.csv
filePath        : /Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/csv
name            : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/csv/user.csv
mimeType        : text/csv
uri             : file:/Users/sgoeschl/work/github/apache/freemarker-generator/freemarker-generator-cli/target/appassembler/examples/data/csv/user.csv
group           : default

Iterating Over Properties Of A Datasource
---------------------------------------------------------------------------
format          : DEFAULT

```






