Apache FreeMarker Generator Maven Plugin
=============================================================================

This plugin generates source files from FreeMarker templates with a flexible process that includes the ability to:

- Generate multiple source files from a single template,
- Generate source files during multiple steps in the build process such as testing, and
- Specify distinct locations for the templates and data models for different stages of the build. 

## Install

### pom.xml

Add the following snippet within the `<plugins>` tag of your pom.xml:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.freemarker.generator</groupId>
            <artifactId>freemarker-generator-maven-plugin</artifactId>
            <version>${project.version}</version>
            <configuration>
                <!-- Required. Specifies the compatibility version for template processing -->
                <freeMarkerVersion>${freemarker.version}</freeMarkerVersion>
            </configuration>
            <executions>
                <!-- 
                    If you want to generate files during other phases, just add more execution
                    tags and specify appropriate phase, sourceDirectory and outputDirectory values.
                -->
                <execution>
                    <id>freemarker-generator</id>
                    <!-- Optional, defaults to generate-sources -->
                    <phase>generate-sources</phase>
                    <goals>
                        <!-- Required, must be generate -->
                        <goal>generate</goal>
                    </goals>
                    <configuration>
                        <!-- Optional, defaults to src/main/freemarker/generator -->
                        <sourceDirectory>src/main/freemarker/generator</sourceDirectory>
                        <!-- Optional, defaults to src/main/freemarker/generator/templatee -->
                        <templateDirectory>src/main/freemarker/generator/template</templateDirectory>
                        <!-- Optional, defaults to src/main/freemarker/generator/generatorr -->
                        <generatorDirectory>src/main/freemarker/generator/generator</generatorDirectory>
                        <!-- Optional, defaults to target/generated-sources/freemarker/generator -->
                        <outputDirectory>target/generated-sources/freemarker/generator</outputDirectory>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## Usage

### FreeMarker Template Files
FreeMarker template files must reside in the `templateDirectory`. For the default configuration,
this is: `src/main/freemarker/generator/template`.

By convention, file names for FreeMarker template files use the .ftl extension. For details on the FreeMarker
template syntax, see: [Getting Started](https://freemarker.apache.org/docs/dgui_quickstart.html) and
[Template Language Reference](https://freemarker.apache.org/docs/ref.html).

### JSON Generator Files
The JSON generator files must reside in the `generatorDirectory`. For the default
configuration, this is: `src/main/freemarker/generator/generator`.

For each JSON generator file, freemarker-maven-plugin will generate a file under the outputDirectory.
The name of the generated file will be based on the name of the JSON data file. For example,
the following JSON file: 
```
    <sourceDirectory>/data/my/package/MyClass.java.json
```
will result in the following file being generated:
```
    <outputDirectory>/my/package/MyClass.java
```

This plugin parses the JSON generator file's `dataModel` field into a `Map<String, Object>` instance (hereafter, referred
to as the data model). If the dataModel field is empty, an empty map will be created.

Here are some additional details you need to know.

  - This plugin *requires* one top-level field in the JSON data file: `templateName`. This field is used to locate the template file under `<sourceDirectory>/template` that is used to generate the file. This plugin provides the data model to FreeMarker as the data model to process the template identified by `templateName`.
  - The parser allows for comments.
  - This plugin currently assumes that the JSON data file encoded using UTF-8.

Here is an example JSON data file:
```json
{
  // An end-of-line comment.
  # Another end-of-line comment
  "templateName": "my-template.ftl", #Required
  "dataModel": { #Optional
      /* A multi-line
         comment */
      "myString": "a string",
      "myNumber": 1,
      "myListOfStrings": ["s1", "s2"],
      "myListOfNumbers": [1, 2],
      "myMap": {
        "key1": "value1",
        "key2": 2
      }
  }
}
```

### Using POM Properties During Generation
After parsing the JSON file, the plugin will add
a `pomProperties` entry into the data model, which is a map itself, that contains the properties defined in the pom. Thus, your template can reference the pom property `my_property` using `${pomProperties.my_property}`. If you have a period or dash in the property name, use `${pomProperties["my.property"]}`.

### FreeMarker Configuration

Typically, users of this plugin do not need to mess with the FreeMarker configuration. This plugin explicitly sets two FreeMarker configurations:

 1. the default encoding is set to UTF-8
 2. the template loader is set to be a FileTemplateLoader that reads from `templateDirectory`.
 
If you need to override these configs or set your own, you can put them in a 
`<sourceDirectory>/freemarker.properties` file. If that file exists, this plugin will read it into a java Properties instance and pass it to freemarker.core.Configurable.setSettings() to establish the FreeMarker configuration. See this [javadoc](https://freemarker.apache.org/docs/api/freemarker/template/Configuration.html#setSetting-java.lang.String-java.lang.String-) for configuration details.


### Incremental Builds
This plugin supports incremental builds; it only generates sources if the generator file, template file, or pom file have timestamps newer than any existing output file.  To force a rebuild if these conditions are not met (for example, if you pass in a model parameter on the command line), first run `mvn clean`.

## Code Coverage

By default, the code coverage report is not generated. It is generated by screwdriver jobs. You can generate code coverage on your dev machine with the following maven command:
```bash
mvn clean initialize -Dclover-phase=initialize 
``` 
Bring up the coverage report by pointing your browser to target/site/clover/dashboard.html under the root directory of the local repository.
