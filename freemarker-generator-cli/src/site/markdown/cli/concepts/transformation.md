## Transformation

The `freemarker-cli` generates text output based on FreeMarker templates and data

* A command line invocation requires 1..n `templates` and 0..n `data sources` / `data models` 
* A command line invocation is mapped to a series of `transformations`
* The `transformation` consists of exactly one `template`, 0..n `data sources` / `data models` and an `output`
* An `output` is either written to 
    * `stdout`
    * an output file
    * an output directory
* When the output is written to a directory
    * the structure of the input directory is preserved
    * a `ftl` file extension is removed

Transforming a single template and data source to `stdout`

```
freemarker-cli \
-t templates/csv/md/transform.ftl examples/data/csv/contract.csv
```

Transforming multiple templates to multiple output files

```
freemarker-cli \
-t templates/csv/md/transform.ftl -o target/contract.md \
-t templates/csv/html/transform.ftl -o target/contract.html \
examples/data/csv/contract.csv
```

Transforming single template directory to single output directory

```
freemarker-cli \ 
-P NGINX_HOSTNAME=localhost \
-t examples/data/template -o target/out/template1
```

Transforming multiple template directories to multiple output directories

```
freemarker-cli \ 
-P NGINX_HOSTNAME=localhost \
-t examples/data/template -o target/out/template1 \
-t examples/data/template -o target/out/template2 
```
