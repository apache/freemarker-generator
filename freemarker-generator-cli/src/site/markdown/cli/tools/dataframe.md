# DataFrameTool

The `DataFrameTool` uses [nRo/DataFrame](https://github.com/nRo/DataFrame) to convert tabular data into a `DataFrame`.

A `DataFrame` allows declartive filtering and transformation of tabular data, i.e. little code to write.

Currently the following sources are supported

* Apache Commons CSV Parser
* JSON arrays
* Excel sheets (to be done)

## Examples

[nRo/DataFrame]("https://raw.githubusercontent.com/nRo/DataFrame/master/src/test/resources/users.csv") provides the following CSV file

```
┌────────────┬────────────┬────────────┐
│#name       │#age        │#country    │
├────────────┼────────────┼────────────┤
│Schmitt     │24          │Germany     │
├────────────┼────────────┼────────────┤
│Parker      │45          │USA         │
├────────────┼────────────┼────────────┤
│Meier       │20          │Germany     │
├────────────┼────────────┼────────────┤
│Schmitt     │30          │France      │
├────────────┼────────────┼────────────┤
│Peter       │44          │Germany     │
├────────────┼────────────┼────────────┤
│Meier       │24          │Germany     │
├────────────┼────────────┼────────────┤
│Green       │33          │UK          │
├────────────┼────────────┼────────────┤
│Schmitt     │30          │Germany     │
├────────────┼────────────┼────────────┤
│Meier       │30          │Germany     │
└────────────┴────────────┴────────────┘
```

and create a `DateFrame` using the following code

```
<#assign cvsFormat = CSVTool.formats["DEFAULT"].withHeader().withDelimiter(';')>
<#assign csvParser = CSVTool.parse(DataSources.get(0), cvsFormat)>
<#assign users = DataFrameTool.toDataFrame(csvParser)>
```

### Select & Sort

Now we want to create a new `DataFrame` by selecting `name` and `country`

```
<#assign country = "Germany">
${DataFrameTool.print(users
    .select("(name == 'Schmitt' || name == 'Meier') && country == '${country}'")
    .sort("name", DataFrameTool.sortOrder["ASCENDING"]))}
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

### Count Column Values

Let's assume we want to count the records for each `country`

```
${DataFrameTool.print(users.getColumn("country").transform(DataFrameTool.transformer["COUNT"]))}
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

### Group By Age And Country

Let's assume that we want to group the `DataFrame` by `age` and `country`

```
${DataFrameTool.print(users.groupBy("age", "country").sort("age"))}
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