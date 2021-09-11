## Transformation

The `freemarker-generator` generates text output based on processing FreeMarker templates and data 

* A command line invocation requires 1..n `templates` and 0..n `data sources` / `data models` 
* A command line invocation is internally mapped to a list of `output generators`
* The `output generator` consists of exactly one `template`, 0..n `data sources` / `data models` written to an `output` destination
* The `output` of an `output generator` is either written to
    * `stdout`
    * an output file
    * an output directory
* When the output is written to a directory
    * the structure of the input directory is preserved
    * any `ftl` file extension is removed
* Positional command line arguments are interpreted as `data sources` (or directories) and accessible by a all `output generators`   

### Examples

Transform a single template to a single output file 

```
freemarker-generator -t freemarker-generator/csv/md/transform.ftl -o target/contract.md examples/data/csv/contract.csv 
```

Transform multiple templates to multiple output files (1:1 mapping between templates and outputs)

```
freemarker-generator \
-t freemarker-generator/csv/md/transform.ftl -o target/contract.md \
-t freemarker-generator/csv/html/transform.ftl -o target/contract.html \
examples/data/csv/contract.csv

> tree target 
target
|-- contract.html
`-- contract.md
```

Transform a single template directory to single output directory

```
> freemarker-generator \
-t examples/data/template -o target/template1

> tree target     
target
`-- template1
    |-- application.properties
    `-- nginx
        `-- nginx.conf
```

Transforming multiple template directories to multiple output directories

```
freemarker-generator \
-t examples/data/template -o target/template1 \
-t examples/data/template -o target/template2 

> tree target     
target
|-- template1
|   |-- application.properties
|   `-- nginx
|       `-- nginx.conf
`-- template2
    |-- application.properties
    `-- nginx
        `-- nginx.conf
```

Defining multiple transformation on the command line can be clumsy but [Picolic's @-Files](https://picocli.info/#AtFiles) can help - the following `@-File` defined, e.g. `@examples.args`

```
#############################################################################
# CSV
#############################################################################

-t freemarker-generator/csv/html/transform.ftl -s examples/data/csv/contract.csv -o target/out/contract.html
-t freemarker-generator/csv/md/transform.ftl -s examples/data/csv/contract.csv -o target/out/contract.md
```

than multiple transformation can be invoked using the following command line

```
bin/freemarker-generator @examples.args 
```
