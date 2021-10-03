## DataFrames

A `DataFrame` allows declarative filtering and transformation of tabular data, i.e. less code to write. 

The `DataFrameTool` uses [nRo/DataFrame](https://github.com/nRo/DataFrame) to convert tabular data into a `DataFrame`.

Currently the following sources are supported

* Apache Commons CSV Parser
* JSON arrays represented as collection of maps
* Excel sheets represented as rows

### Working With CSV

[nRo/DataFrame]("https://raw.githubusercontent.com/nRo/DataFrame/master/src/test/resources/users.csv") provides the following CSV file

```
name;age;country
Schmitt;24;Germany
Parker;45;USA
Meier;20;Germany
Schmitt;30;France
Peter;44;Germany
Meier;24;Germany
Green;33;UK
Schmitt;30;Germany
Meier;30;Germany
```

and create a `DateFrame` using the following code snippet

```
<#assign dataSource = dataSources[0]>
<#assign csvParser = tools.csv.parse(dataSource, tools.csv.formats["DATAFRAME"])>
<#assign users = tools.dataframe.fromCSVParser(csvParser)>
```

The example can be executed by running

```
freemarker-generator \
-PCSV_SOURCE_FORMAT=DATAFRAME \
-t examples/templates/dataframe/example.ftl \
examples/data/csv/dataframe.csv
```

or

```
freemarker-generator \
-PCSV_SOURCE_FORMAT=DATAFRAME \
-t examples/templates/dataframe/example.ftl \
https://raw.githubusercontent.com/nRo/DataFrame/master/src/test/resources/users.csv
```

#### Select By Age

```
${tools.dataframe.print(users.select("(age > 40)"))}
```

which shows 

```
┌────────────┬────────────┬────────────┐
│#name       │#age        │#country    │
├────────────┼────────────┼────────────┤
│Parker      │45          │USA         │
├────────────┼────────────┼────────────┤
│Peter       │44          │Germany     │
└────────────┴────────────┴────────────┘
```

#### Complex Select & Sort

Now we want to create a new `DataFrame` by selecting `name` and `country`

```
<#assign country = "Germany">
${tools.dataframe.print(users
    .select("(name == 'Schmitt' || name == 'Meier') && country == '${country}'")
    .sort("name", tools.dataframe.sortOrder["ASCENDING"]))}
```

which shows

```
┌────────────┬────────────┬────────────┐
│#name       │#age        │#country    │
├────────────┼────────────┼────────────┤
│Meier       │20          │Germany     │
├────────────┼────────────┼────────────┤
│Meier       │24          │Germany     │
├────────────┼────────────┼────────────┤
│Meier       │30          │Germany     │
├────────────┼────────────┼────────────┤
│Schmitt     │24          │Germany     │
├────────────┼────────────┼────────────┤
│Schmitt     │30          │Germany     │
└────────────┴────────────┴────────────┘
```

#### Count Column Values

Let's assume we want to count the records for each `country`

```
${tools.dataframe.print(users.getColumn("country").transform(tools.dataframe.transformer["COUNT"]))}
```

returns the following `DataFrame`

```
┌────────────┬────────────┐
│#country    │#counts     │
├────────────┼────────────┤
│Germany     │6           │
├────────────┼────────────┤
│USA         │1           │
├────────────┼────────────┤
│France      │1           │
├────────────┼────────────┤
│UK          │1           │
└────────────┴────────────┘
```

#### Group By Age And Country

Let's assume that we want to group the `DataFrame` by `age` and `country`

```
${tools.dataframe.print(users.groupBy("age", "country").sort("age"))}
``` 

which results in 

```
┌────────────┬────────────┐
│#age        │#country    │
├────────────┼────────────┤
│20          │Germany     │
├────────────┼────────────┤
│24          │Germany     │
├────────────┼────────────┤
│30          │France      │
├────────────┼────────────┤
│30          │Germany     │
├────────────┼────────────┤
│33          │UK          │
├────────────┼────────────┤
│44          │Germany     │
├────────────┼────────────┤
│45          │USA         │
└────────────┴────────────┘
```

### Working With JSON

Here we load a `examples/data/json/github-users.json` which represents a tabular 
data being parsed as a list of maps and print the JSON as dataframe. Technically
it is a list of maps hence we invoke `tools.dataframe.fromMaps()

```
freemarker-generator \
-i '${tools.dataframe.print(tools.dataframe.fromMaps(tools.gson.parse(dataSources[0])))}' \
examples/data/json/github-users.json

┌────────────┬────────────┬────────────┬────────────┬────────────┬────────────┬────────────┬────────────┬────────────┬────────────┬────────────┬────────────┬────────────┬────────────┬────────────┬────────────┬────────────┐
│#login      │#id         │#avatar_ur  │#gravatar_  │#url        │#html_url   │#followers  │#following  │#gists_url  │#starred_u  │#subscript  │#organizat  │#repos_url  │#events_ur  │#received_  │#type       │#site_admi  │
├────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┤
│mojombo     │1.00000000  │https:/...  │            │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │User        │false       │
├────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┤
│defunkt     │2.00000000  │https:/...  │            │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │User        │true        │
├────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┤
│pjhyett     │3.00000000  │https:/...  │            │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │User        │true        │
├────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┤
│wycats      │4.00000000  │https:/...  │            │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │User        │false       │
├────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┤
│ezmobius    │5.00000000  │https:/...  │            │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │User        │false       │
├────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┤
│ivey        │6.00000000  │https:/...  │            │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │User        │false       │
├────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┤
│evanphx     │7.00000000  │https:/...  │            │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │https:/...  │User        │false       │
└────────────┴────────────┴────────────┴────────────┴────────────┴────────────┴────────────┴────────────┴────────────┴────────────┴────────────┴────────────┴────────────┴────────────┴────────────┴────────────┴────────────┘
```

### Working With Excel

Let's transform an Excel Sheet to a `DataFrame` being printed using the following template

```
<#assign dataSource = dataSources[0]>
<#assign workbook = tools.excel.parse(dataSource)>
<#list tools.excel.getSheets(workbook) as sheet>
    <#assign table = tools.excel.toTable(sheet)>
    <#assign df = tools.dataframe.fromRows(table, true)>
    ${tools.dataframe.print(df)}<#t>
</#list>
```

which is rendered by the following command line invocation

```
freemarker-generator -t examples/templates/excel/dataframe/transform.ftl examples/data/excel/test.xls

┌────────────┬────────────┬────────────┬────────────┬────────────┬────────────┬────────────┐
│#Text       │#Date       │#Number     │#Currency   │#Time       │#Percentag  │#Forumula   │
├────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┤
│Row 1       │01/01/17    │100.00      │€100.00     │10:00       │50.00%      │C2*F2       │
├────────────┼────────────┼────────────┼────────────┼────────────┼────────────┼────────────┤
│Row 2       │01/01/17    │100.00      │€100.00     │10:00       │50.00%      │C3*F3       │
└────────────┴────────────┴────────────┴────────────┴────────────┴────────────┴────────────┘
```