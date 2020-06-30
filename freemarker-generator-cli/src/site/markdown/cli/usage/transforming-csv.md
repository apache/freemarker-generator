## Transforming CSV

A common task is changing the output format of a CSV file therefore `Apache FreeMarker CLI` ships with a ready-to-use
templates to convert CSVs

* Convert a CSV into a different format
* Convert a CSV into Markdown or HTML

### Convert CSV To A Different Output Format

Let's assume that a CSV file in [DEFAULT format](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html#DEFAULT) 
should be converted into a [Microsoft Excel format](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html#EXCEL).
This allows opening the CSV directly in Excel without going to the tedious CSV import dialog.

The following command line prints the converted CSV to `stdout`

```
freemarker-cli \
 -PCSV_SOURCE_FORMAT=DEFAULT \
 -PCSV_TARGET_FORMAT=EXCEL \
 -PCSV_TARGET_DELIMITER=SEMICOLON \
 -t templates/csv/csv/transform.ftl \
 https://raw.githubusercontent.com/apache/freemarker-generator/master/freemarker-generator-cli/examples/data/csv/contract.csv 
```  

The command line invocation seems a bit complex at first so let's look at it more closely

* `CSV_SOURCE_FORMAT` defines the CSV source format for reading the CSV
* `CSV_TARGET_FORMAT` defines the CSV tagrte format for writing the CSV
* `CSV_TARGET_DELIMITER` explicitely sets the delimiter of the target CSV to a semicolon since this expected by Excel for my current locale

### Convert CSV To Markdown

When doing documentation it is sometimes helpful to convert a CSV into its Markdown representation (AsciiDoc 
actually allows including CSV directly)

The following command line prints the resulting MarkDown to `stdout`

```
freemarker-cli \
 -PCSV_SOURCE_FORMAT=DEFAULT \
 -t templates/csv/md/transform.ftl \
 https://raw.githubusercontent.com/apache/freemarker-generator/master/freemarker-generator-cli/examples/data/csv/contract.csv 
```  

Please note that most MarkDown tools expect a header row (see [Create a table without a header in Markdown](https://stackoverflow.com/questions/17536216/create-a-table-without-a-header-in-markdown))

### Convert CSV To HTML

Of course it is possible to convert a CSV to HTML as well

```
freemarker-cli \
 -PCSV_SOURCE_FORMAT=DEFAULT \
 -t templates/csv/html/transform.ftl \
 https://raw.githubusercontent.com/apache/freemarker-generator/master/freemarker-generator-cli/examples/data/csv/contract.csv 
```  

### CSV Configuration Options

The following options can be passed to template (as user-supplied parameters)

| Parameter                 | Default Value     | Description                                               |
|---------------------------|-------------------|-----------------------------------------------------------|
| CSV_SOURCE_FORMAT         | DEFAULT           | Source CSV format                                         |
| CSV_SOURCE_DELIMITER      | COMMA             | Symbolic name of delimiter, e.g. "COLON" or "SEMICOLON"   |
| CSV_SOURCE_WITH_HEADER    | true              | Whether the first rows are headers                        |
| CSV_TAGRGET_FORMAT        | DEFAULT           | Target CSV format                                         |
| CSV_TARGET_DELIMITER      | COMMA             | Symbolic name of delimiter, e.g. "COLON" or "SEMICOLON"   |
| CSV_TARGET_WITH_HEADER    | true              | Whether the first rows are headers                        |
                                                            
The delimiters are passed as symbolic names since passing seperators breaks command line handling

* COMMA
* HASH
* PIPE
* RS (ASCII record separator)
* SEMICOLON
* SPACE
* TAB
