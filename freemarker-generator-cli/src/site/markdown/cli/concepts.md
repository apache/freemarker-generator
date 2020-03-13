## The Mental Model

* A command line invocation requires 1..n `templates` and 0..n `datasources`
* A command line invocation is mapped to a series of `transformations`
* The mapping strategy is either
    * `aggregating`: each `template` yields one output file based on 0..n `datasources`
    * `generating`: each `datasources` yields one output per `template`
* The `transformation` consists of exactly one `template`, 0..n `datasources` and an `output`
* An `output` is either written to 
    * `stdout`
    * an output file
    * an output directory
* When the output is written to a directory
    * the structure of the input directory is preserved
    * the file names can be customized using an output mapping

## Aggregation Versus Generation

This distiniction is only relevant if you have more than one `datasource` for a given `template`

* aggregation will invoke the `template` once with all `datasources`
* generation will invoke the `template` multiple times with only one `datasources`

### Aggregation

A nice example for `aggregation` is the generation of test user documentation with the following directory layout

```text
.
|-- data
|   |-- user-products.csv
|   |-- user-transactions.csv
|   `-- users.csv
`-- templates
    |-- test-user-documentation.html.ftl
    `-- test-user-documentation.md.ftl
```

We want to transform those three CSV input files into a `Markdown` and `HTML` document copied into an `out` directory

```text
# Using short options
> freemarker-cli -t templates -d data -output-dir out

# Using long options
> freemarker-cli -m aggregate -t templates -d data -output-dir out
> freemarker-cli --mode aggregate --template templates --datasource data -output-dir out
```

The invocation would generate the following files

```text
.
`-- out
    |-- test-user-documentation.html
    `-- test-user-documentation.md
```

### Generation

#### Source Code Generation

A good example for `generation` is the creation of souce code, e.g. as provided by the `maven-generator-plugin`

```text
.
|-- data
|   |-- ProductDao.java.json
|   |-- TransactionDao.java.json
|   `-- UserDao.java.json
`-- templates
    `-- generate-dao.ftl
```

We want to generate three Java files based on JSON files and a single template

```text
# Using short options
> freemarker-cli -m generate -t templates -d data -output-dir out

# Using long options
> freemarker-cli --mode generate --template templates --datasource data -output-dir out
```

The invocation would generate the following files

```text
.
`-- out
    |-- ProductDao.java
    |-- TransactionDao.java
    `-- UserDao.java
```

#### Configuration File Generations

Another example for `generation` comes from cloud computing, i.e. the generation of configuration files using the following directory layout

```text
.
|-- config
|   |-- application.yml
|   `-- nginx.conf
`-- config.json
```

We want to generate a set of expanded configuration files

```text
# Using short options
> freemarker-cli -m generate -t config -d config.json -output-dir out

# Using long options
> freemarker-cli --mode generate --template templates --datasource data -output-dir out
```

which results in the following directory structure

```text
.
`-- out
|   |-- application.yml
|   `-- nginx.conf
```
