Apache FreeMarker Generator Maven Plugin Sample
=============================================================================

This project allows to play around with `freemarker-generator-maven-plugin` quickly. 

Let's create a Java class and text file using the `freemarker-generator-maven-plugin`, compile and package it (so you know that the generated code is compiling)

```text
> mvn clean package
> cat target/generated-sources/freemarker/generator/org/apache/freemarker/generator/maven/sample/HelloWorld.java 
> cat target/generated-sources/freemarker/generator/test.txt
```

