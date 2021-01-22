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
* Positional command line arguments are interpreted as `data sources` (or directories) and accessible by a `output generators`   

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

Transforming multiple templates and data sources to multiple output files

```
freemarker-generator \
-t freemarker-generator/yaml/json/transform.ftl -s examples/data/yaml/customer.yaml -o customer.json \
-t freemarker-generator/yaml/json/transform.ftl -s examples/data/yaml/swagger-spec.yaml -o swagger-spec.json

> ls -l *.json
-rw-r--r--  1 sgoeschl  staff   332B Jan  5 21:30 customer.json
-rw-r--r--  1 sgoeschl  staff    25K Jan  5 21:30 swagger-spec.json
```  
