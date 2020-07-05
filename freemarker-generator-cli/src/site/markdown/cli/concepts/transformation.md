## Transformation

The `freemarker-cli` generates text output based on processing FreeMarker templates and data 

* A command line invocation requires 1..n `templates` and 0..n `data sources` / `data models` 
* A command line invocation is mapped to a series of `template transformations`
* The `transformation` consists of exactly one `template`, 0..n `data sources` / `data models` written to an `output`
* The `output` is either written to
    * `stdout`
    * one or more output files
    * one or output directories
* When the output is written to a directory
    * the structure of the input directory is preserved
    * a `ftl` file extension is removed

### Examples

Transforming a single template to a single output file 

```
freemarker-cli \
-t templates/csv/md/transform.ftl examples/data/csv/contract.csv \
-o target/contract.md
```

Transforming multiple templates to multiple output files (1:1 mapping between templates and outputs)

```
> freemarker-cli \
-t templates/csv/md/transform.ftl -o target/contract.md \
-t templates/csv/html/transform.ftl -o target/contract.html \
examples/data/csv/contract.csv

> tree target 
target
|-- contract.html
`-- contract.md
```

Transforming single template directory to single output directory

```
> freemarker-cli \
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
freemarker-cli \
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
