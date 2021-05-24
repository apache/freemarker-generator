#!/bin/sh

#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Check that java is on the path

hash java 2>/dev/null || { echo >&2 "I require JDK but it's not installed.  Aborting."; exit 1; }

# Run all the samples being documented

mkdir target 2>/dev/null
mkdir target/out 2>/dev/null

FREEMARKER_CMD=./bin/freemarker-generator

#############################################################################
# Info
#############################################################################

echo "templates/freemarker-generator/info.ftl"
$FREEMARKER_CMD -t freemarker-generator/info.ftl README.md > target/out/info.txt || { echo >&2 "Test failed.  Aborting."; exit 1; }

#############################################################################
# Demo
#############################################################################

echo "examples/templates/demo.ftl"
$FREEMARKER_CMD -t examples/templates/demo.ftl README.md > target/out/demo.txt || { echo >&2 "Test failed.  Aborting."; exit 1; }

#############################################################################
# DataSources
#############################################################################

echo "examples/templates/datasources.ftl"
$FREEMARKER_CMD -t examples/templates/datasources.ftl -s :data=examples/data > target/out/datasources-01.txt || { echo >&2 "Test failed.  Aborting."; exit 1; }

echo "examples/templates/datasources.ftl"
$FREEMARKER_CMD -t examples/templates/datasources.ftl -s https://xkcd.com/info.0.json https://www.google.com > target/out/datasources-02.txt || { echo >&2 "Test failed.  Aborting."; exit 1; }

#############################################################################
# Interactive Mode
#############################################################################

$FREEMARKER_CMD -i '${tools.jsonpath.parse(dataSources?values[0]).read("$.info.title")}' examples/data/json/swagger-spec.json > target/out/interactive-json.txt || { echo >&2 "Test failed.  Aborting."; exit 1; }
$FREEMARKER_CMD -i '${tools.xml.parse(dataSources?values[0])["recipients/person[1]/name"]}' examples/data/xml/recipients.xml > target/out/interactive-xml.txt || { echo >&2 "Test failed.  Aborting."; exit 1; }
$FREEMARKER_CMD -i '${tools.jsoup.parse(dataSources?values[0]).select("a")[0]}' examples/data/html/dependencies.html > target/out/interactive-html.txt || { echo >&2 "Test failed.  Aborting."; exit 1; }
$FREEMARKER_CMD -i '${tools.gson.toJson(tools.yaml.parse(dataSources?values[0]))}' examples/data/yaml/swagger-spec.yaml > target/out/interactive-swagger.json || { echo >&2 "Test failed.  Aborting."; exit 1; }
$FREEMARKER_CMD -i '${tools.yaml.toYaml(tools.gson.parse(dataSources?values[0]))}' examples/data/json/swagger-spec.json > target/out/interactive-swagger.yaml || { echo >&2 "Test failed.  Aborting."; exit 1; }
$FREEMARKER_CMD -i '${tools.dataframe.print(tools.dataframe.fromMaps(tools.gson.parse(dataSources?values[0])))}' examples/data/json/github-users.json > target/out/interactive-dataframe.txt || { echo >&2 "Test failed.  Aborting."; exit 1; }

#############################################################################
# CSV
#############################################################################

echo "templates/freemarker-generator/csv/html/transform.ftl"
$FREEMARKER_CMD -t freemarker-generator/csv/html/transform.ftl examples/data/csv/contract.csv > target/out/contract.html || { echo >&2 "Test failed.  Aborting."; exit 1; }

echo "templates/freemarker-generator/csv/md/transform.ftl"
$FREEMARKER_CMD -t freemarker-generator/csv/md/transform.ftl examples/data/csv/contract.csv > target/out/contract.md || { echo >&2 "Test failed.  Aborting."; exit 1; }

echo "examples/templates/csv/shell/curl.ftl"
$FREEMARKER_CMD -t ./examples/templates/csv/shell/curl.ftl examples/data/csv/user.csv > target/out/curl.sh || { echo >&2 "Test failed.  Aborting."; exit 1; }

echo "examples/templates/csv/md/filter.ftl"
$FREEMARKER_CMD -e UTF-8 -l de_AT -Pcolumn="Order ID" -Pvalues=226939189,957081544 -Pformat=DEFAULT -Pdelimiter=COMMA -t examples/templates/csv/md/filter.ftl examples/data/csv/sales-records.csv > target/out/sales-records.md || { echo >&2 "Test failed.  Aborting."; exit 1; }

#############################################################################
# CSV To XML-FO & PDF
#############################################################################

echo "examples/templates/csv/fo/transform.ftl"
$FREEMARKER_CMD -t examples/templates/csv/fo/transform.ftl examples/data/csv/locker-test-users.csv > target/out/locker-test-users.fo || { echo >&2 "Test failed.  Aborting."; exit 1; }

if hash fop 2>/dev/null; then
	echo "fop -fo target/out/locker-test-users.fo target/out/locker-test-users.pdf"
    fop -fo target/out/locker-test-users.fo target/out/locker-test-users.pdf 2>/dev/null || { echo >&2 "Test failed.  Aborting."; exit 1; }
fi

echo "examples/templates/csv/fo/transactions.ftl"
$FREEMARKER_CMD -t examples/templates/csv/fo/transactions.ftl examples/data/csv/transactions.csv > target/out/transactions.fo || { echo >&2 "Test failed.  Aborting."; exit 1; }

if hash fop 2>/dev/null; then
	echo "fop -fo target/out/transactions.fo target/out/transactions-fo.pdf"
    fop -fo target/out/transactions.fo target/out/transactions-fo.pdf 2>/dev/null || { echo >&2 "Test failed.  Aborting."; exit 1; }
fi

#############################################################################
# CSV to HTML & PDF
#############################################################################

echo "examples/templates/csv/html/transform.ftl"
$FREEMARKER_CMD -t examples/templates/csv/html/transactions.ftl examples/data/csv/transactions.csv > target/out/transactions.html || { echo >&2 "Test failed.  Aborting."; exit 1; }

if hash wkhtmltopdf 2>/dev/null; then
	echo "wkhtmltopdf -O landscape target/out/transactions.html target/out/transactions-html.pdf"
    wkhtmltopdf -O landscape target/out/transactions.html target/out/transactions-html.pdf 2>/dev/null || { echo >&2 "Test failed.  Aborting."; exit 1; }
fi

#############################################################################
# DataFrame
#############################################################################

echo "examples/templates/dataframe/example.ftl"
$FREEMARKER_CMD -PCSV_SOURCE_DELIMITER=SEMICOLON -PCSV_SOURCE_WITH_HEADER=true -t examples/templates/dataframe/example.ftl examples/data/csv/dataframe.csv > target/out/dataframe.txt || { echo >&2 "Test failed.  Aborting."; exit 1; }

#############################################################################
# Grok
#############################################################################

echo "examples/templates/accesslog/combined-access.ftl"
$FREEMARKER_CMD -t examples/templates/accesslog/combined-access.ftl examples/data/accesslog/combined-access.log > target/out/combined-access.log.txt || { echo >&2 "Test failed.  Aborting."; exit 1; }

echo "examples/templates/logs/csv/serverlog-to-csv.ftl"
$FREEMARKER_CMD -t examples/templates/logs/csv/serverlog-to-csv.ftl examples/data/logs > target/out/server.log.csv || { echo >&2 "Test failed.  Aborting."; exit 1; }

#############################################################################
# Excel
#############################################################################

echo "examples/templates/excel/dataframe/transform.ftl"
$FREEMARKER_CMD -t examples/templates/excel/dataframe/transform.ftl examples/data/excel/test.xls > target/out/test.xls.dataframe.txt || { echo >&2 "Test failed.  Aborting."; exit 1; }

echo "templates/freemarker-generator/excel/html/transform.ftl"
$FREEMARKER_CMD -t freemarker-generator/excel/html/transform.ftl examples/data/excel/test.xls > target/out/test.xls.html || { echo >&2 "Test failed.  Aborting."; exit 1; }
$FREEMARKER_CMD -t freemarker-generator/excel/html/transform.ftl examples/data/excel/test.xlsx > target/out/test.xslx.html || { echo >&2 "Test failed.  Aborting."; exit 1; }
$FREEMARKER_CMD -t freemarker-generator/excel/html/transform.ftl examples/data/excel/test-multiple-sheets.xlsx > target/out/test-multiple-sheets.xlsx.html || { echo >&2 "Test failed.  Aborting."; exit 1; }

echo "templates/freemarker-generator/excel/md/transform.ftl"
$FREEMARKER_CMD -t freemarker-generator/excel/md/transform.ftl examples/data/excel/test-multiple-sheets.xlsx > target/out/test-multiple-sheets.xlsx.md || { echo >&2 "Test failed.  Aborting."; exit 1; }

echo "templates/freemarker-generator/excel/csv/transform.ftl"
$FREEMARKER_CMD -t freemarker-generator/excel/csv/transform.ftl examples/data/excel/test-multiple-sheets.xlsx > target/out/test-multiple-sheets.xlsx.csv || { echo >&2 "Test failed.  Aborting."; exit 1; }

echo "examples/templates/excel/csv/custom.ftl"
$FREEMARKER_CMD -t examples/templates/excel/csv/custom.ftl -Pcsv.format=MYSQL examples/data/excel/test.xls > target/out/test-transform-xls.csv || { echo >&2 "Test failed.  Aborting."; exit 1; }

#############################################################################
# HTML
#############################################################################

echo "examples/templates/html/csv/dependencies.ftl"
$FREEMARKER_CMD -t examples/templates/html/csv/dependencies.ftl examples/data/html/dependencies.html > target/out/dependencies.csv || { echo >&2 "Test failed.  Aborting."; exit 1; }

echo "examples/templates/html/txt/licence.ftl"
$FREEMARKER_CMD -t examples/templates/html/txt/licence.ftl examples/data/html/dependencies.html > target/out/licence.txt || { echo >&2 "Test failed.  Aborting."; exit 1; }

#############################################################################
# Java Faker
#############################################################################

echo "examples/templates/javafaker/csv/testdata.ftl"
$FREEMARKER_CMD -DNR_OF_RECORDS=10 -t examples/templates/javafaker/csv/testdata.ftl > target/out/testdata.csv || { echo >&2 "Test failed.  Aborting."; exit 1; }

#############################################################################
# JSON
#############################################################################

echo "examples/templates/json/csv/swagger-endpoints.ftl"
$FREEMARKER_CMD -t examples/templates/json/csv/swagger-endpoints.ftl examples/data/json/swagger-spec.json > target/out/swagger-spec.csv || { echo >&2 "Test failed.  Aborting."; exit 1; }

echo "templates/freemarker-generator/json/yaml/transform.ftl"
$FREEMARKER_CMD -t freemarker-generator/json/yaml/transform.ftl examples/data/json/swagger-spec.json > target/out/swagger-spec.yaml || { echo >&2 "Test failed.  Aborting."; exit 1; }

echo "examples/templates/json/md/github-users.ftl"
$FREEMARKER_CMD -t examples/templates/json/md/github-users.ftl examples/data/json/github-users.json > target/out/github-users.md || { echo >&2 "Test failed.  Aborting."; exit 1; }

#############################################################################
# Properties
#############################################################################

echo "examples/templates/properties/csv/locker-test-users.ftl"
$FREEMARKER_CMD -t examples/templates/properties/csv/locker-test-users.ftl examples/data/properties > target/out/locker-test-users.csv || { echo >&2 "Test failed.  Aborting."; exit 1; }

#############################################################################
# Template Directory
#############################################################################

echo "examples/data/template"
$FREEMARKER_CMD -t examples/data/template -PNGINX_HOSTNAME=localhost -o target/out/template  || { echo >&2 "Test failed.  Aborting."; exit 1; }

#############################################################################
# XML
#############################################################################

echo "examples/templates/xml/txt/recipients.ftl"
$FREEMARKER_CMD -t examples/templates/xml/txt/recipients.ftl examples/data/xml/recipients.xml > target/out/recipients.txt || { echo >&2 "Test failed.  Aborting."; exit 1; }

#############################################################################
# YAML
#############################################################################

echo "examples/templates/yaml/txt/transform.ftl"
$FREEMARKER_CMD -t examples/templates/yaml/txt/transform.ftl examples/data/yaml/customer.yaml > target/out/customer.txt || { echo >&2 "Test failed.  Aborting."; exit 1; }

echo "templates/freemarker-generator/yaml/json/transform.ftl"
$FREEMARKER_CMD -t freemarker-generator/yaml/json/transform.ftl examples/data/yaml/swagger-spec.yaml > target/out/swagger-spec.json || { echo >&2 "Test failed.  Aborting."; exit 1; }

echo "Created the following sample files in ./target/out"
ls -l ./target/out
