#!/bin/sh
mvn clean install
cd ./freemarker-generator-cli
sh ./run-samples.sh
cd ../freemarker-generator-maven-plugin-sample
mvn clean package