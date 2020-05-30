## Transformation

* A command line invocation requires 1..n `templates` and 0..n `data sources` / `data models` 
* A command line invocation is mapped to a series of `transformations`
* The `transformation` consists of exactly one `template`, 0..n `data sources` / `data models` and an `output`
* An `output` is either written to 
    * `stdout`
    * an output file
    * an output directory
* When the output is written to a directory
    * the structure of the input directory is preserved
    * a "ftl" file externsion is removed
