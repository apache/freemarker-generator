@ECHO OFF
REM
REM  Licensed to the Apache Software Foundation (ASF) under one or more
REM  contributor license agreements.  See the NOTICE file distributed with
REM  this work for additional information regarding copyright ownership.
REM  The ASF licenses this file to You under the Apache License, Version 2.0
REM  (the "License"); you may not use this file except in compliance with
REM  the License.  You may obtain a copy of the License at
REM
REM       http:\\www.apache.org\licenses\LICENSE-2.0
REM
REM  Unless required by applicable law or agreed to in writing, software
REM  distributed under the License is distributed on an "AS IS" BASIS,
REM  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM  See the License for the specific language governing permissions and
REM  limitations under the License.
REM

REM  Run all the samples being documented

mkdir target 2>NULL
mkdir target\out 2>NULL

SET FREEMARKER_CMD=CALL .\bin\freemarker-generator.bat

REM =========================================================================
REM Info
REM =========================================================================

echo "templates\freemarker-generator\info.ftl"
%FREEMARKER_CMD% -t freemarker-generator\info.ftl README.md > target\out\info.txt

REM =========================================================================
REM Demo
REM =========================================================================

echo "examples\templates\demo.ftl"
%FREEMARKER_CMD% -t examples\templates\demo.ftl README.md --output-encoding CP1252 > target\out\demo.txt

#############################################################################
# DataSources
#############################################################################

echo "examples\templates\datasources.ftl"
$FREEMARKER_CMD -t examples\templates\datasources.ftl -s :data=examples/data > target\out\datasources.txt

REM =========================================================================
REM Interactive Mode
REM =========================================================================

%FREEMARKER_CMD% -i '${tools.jsonpath.parse(dataSources?values[0]).read("""$.info.title""")}' examples\data\json\swagger-spec.json > target\out\interactive-json.txt
%FREEMARKER_CMD% -i '${tools.xml.parse(dataSources?values[0])["""recipients/person[1]/name"""]}' examples\data\xml\recipients.xml > target\out\interactive-xml.txt
%FREEMARKER_CMD% -i '${tools.jsoup.parse(dataSources?values[0]).select("""a""")[0]}' examples\data\html\dependencies.html > target\out\interactive-html.txt
%FREEMARKER_CMD% -i '${tools.gson.toJson(tools.yaml.parse(dataSources?values[0]))}' examples\data\yaml\swagger-spec.yaml > target\out\interactive-swagger.json
%FREEMARKER_CMD% -i '${tools.yaml.toYaml(tools.gson.parse(dataSources?values[0]))}' examples\data\json\swagger-spec.json > target\out\interactive-swagger.yaml
%FREEMARKER_CMD% -i '${tools.dataframe.print(tools.dataframe.fromMaps(tools.gson.parse(dataSources?values[0])))}' examples\data\json\github-users.json > target\out\interactive-dataframe.txt

REM =========================================================================
REM CSV
REM =========================================================================

echo "templates\freemarker-generator\csv\html\transform.ftl"
%FREEMARKER_CMD% -t freemarker-generator\csv\html\transform.ftl examples\data\csv\contract.csv > target\out\contract.html

echo "templates\freemarker-generator\csv\md\transform.ftl"
%FREEMARKER_CMD% -t freemarker-generator\csv\md\transform.ftl examples\data\csv\contract.csv > target\out\contract.md

echo "examples\templates\csv\shell\curl.ftl"
%FREEMARKER_CMD% -t .\examples\templates\csv\shell\curl.ftl examples\data\csv\user.csv > target\out\curl.sh

echo "examples\templates\csv\md\filter.ftl"
%FREEMARKER_CMD% -e UTF-8 -l de_AT -Pcolumn="Order ID" -Pvalues=226939189,957081544 -Pformat=DEFAULT -Pdelimiter=COMMA -t examples\templates\csv\md\filter.ftl examples\data\csv\sales-records.csv > target\out\sales-records.md

REM =========================================================================
REM CSV To XML-FO & PDF
REM =========================================================================

echo "examples\templates\csv\fo\transform.ftl"
%FREEMARKER_CMD% -t examples\templates\csv\fo\transform.ftl examples\data\csv\locker-test-users.csv > target\out\locker-test-users.fo

echo "examples\templates\csv\fo\transactions.ftl"
%FREEMARKER_CMD% -t examples\templates\csv\fo\transactions.ftl examples\data\csv\transactions.csv > target\out\transactions.fo

REM =========================================================================
REM CSV to HTML & PDF
REM =========================================================================

echo "examples\templates\csv\html\transform.ftl"
%FREEMARKER_CMD% -t examples\templates\csv\html\transactions.ftl examples\data\csv\transactions.csv > target\out\transactions.html

REM =========================================================================
REM DataFrame
REM =========================================================================

echo "examples\templates\dataframe\example.ftl"
%FREEMARKER_CMD% -PCSV_SOURCE_DELIMITER=SEMICOLON -PCSV_SOURCE_WITH_HEADER=true -t examples\templates\dataframe\example.ftl examples\data\csv\dataframe.csv > target\out\dataframe.txt

REM =========================================================================
REM Grok
REM =========================================================================

echo "examples\templates\accesslog\combined-access.ftl"
%FREEMARKER_CMD% -t examples\templates\accesslog\combined-access.ftl examples\data\accesslog\combined-access.log > target\out\combined-access.log.txt

REM =========================================================================
REM Excel
REM =========================================================================

echo "examples\templates\excel\dataframe\transform.ftl"
%FREEMARKER_CMD% -t examples\templates\excel\dataframe\transform.ftl examples\data\excel\test.xls > target\out\test.xls.dataframe.txt

echo "templates\freemarker-generator\excel\html\transform.ftl"
%FREEMARKER_CMD% -t freemarker-generator\excel\html\transform.ftl examples\data\excel\test.xls > target\out\test.xls.html
%FREEMARKER_CMD% -t freemarker-generator\excel\html\transform.ftl examples\data\excel\test.xlsx > target\out\test.xslx.html
%FREEMARKER_CMD% -t freemarker-generator\excel\html\transform.ftl examples\data\excel\test-multiple-sheets.xlsx > target\out\test-multiple-sheets.xlsx.html

echo "templates\freemarker-generator\excel\md\transform.ftl"
%FREEMARKER_CMD% -t freemarker-generator\excel\md\transform.ftl examples\data\excel\test-multiple-sheets.xlsx > target\out\test-multiple-sheets.xlsx.md

echo "templates\freemarker-generator\excel\csv\transform.ftl"
%FREEMARKER_CMD% -t freemarker-generator\excel\csv\transform.ftl examples\data\excel\test-multiple-sheets.xlsx > target\out\test-multiple-sheets.xlsx.csv

echo "examples\templates\excel\csv\custom.ftl"
%FREEMARKER_CMD% -t examples\templates\excel\csv\custom.ftl -Pcsv.format=MYSQL examples\data\excel\test.xls > target\out\test-transform-xls.csv

REM =========================================================================
REM HTML
REM =========================================================================

echo "examples\templates\html\csv\dependencies.ftl"
%FREEMARKER_CMD% -t examples\templates\html\csv\dependencies.ftl examples\data\html\dependencies.html > target\out\dependencies.csv

echo "examples\templates\html\txt\licence.ftl"
%FREEMARKER_CMD% -t examples\templates\html\txt\licence.ftl examples\data\html\dependencies.html > target\out\licence.txt

REM =========================================================================
REM Java Faker
REM =========================================================================
echo "examples/templates/javafaker/csv/testdata.ftl"
%FREEMARKER_CMD% -DNR_OF_RECORDS=10 -t examples/templates/javafaker/csv/testdata.ftl > target/out/testdata.csv

REM =========================================================================
REM JSON
REM =========================================================================

echo "examples\templates\json\csv\swagger-endpoints.ftl"
%FREEMARKER_CMD% -t examples\templates\json\csv\swagger-endpoints.ftl examples\data\json\swagger-spec.json > target\out\swagger-spec.csv

echo "templates\freemarker-generator\json\yaml\transform.ftl"
%FREEMARKER_CMD% -t freemarker-generator\json\yaml\transform.ftl examples\data\json\swagger-spec.json > target\out\swagger-spec.yaml

echo "examples\templates\json\md\github-users.ftl"
%FREEMARKER_CMD% -t examples\templates\json\md\github-users.ftl examples\data\json\github-users.json > target\out\github-users.md

REM =========================================================================
REM Properties
REM =========================================================================

echo "examples\templates\properties\csv\locker-test-users.ftl"
%FREEMARKER_CMD% -t examples\templates\properties\csv\locker-test-users.ftl examples\data\properties > target\out\locker-test-users.csv

REM =========================================================================
REM Template Directory
REM =========================================================================

echo "examples\data\template"
%FREEMARKER_CMD% -t examples\data\template -PNGINX_HOSTNAME=localhost -o target\out\template

REM =========================================================================
REM XML
REM =========================================================================

echo "examples\templates\xml\txt\recipients.ftl"
%FREEMARKER_CMD% -t examples\templates\xml\txt\recipients.ftl examples\data\xml\recipients.xml > target\out\recipients.txt

REM =========================================================================
REM YAML
REM =========================================================================

echo "examples\templates\yaml\txt\transform.ftl"
%FREEMARKER_CMD% -t examples\templates\yaml\txt\transform.ftl examples\data\yaml\customer.yaml > target\out\customer.txt

echo "templates\freemarker-generator\yaml\json\transform.ftl"
%FREEMARKER_CMD% -t freemarker-generator\yaml\json\transform.ftl examples\data\yaml\swagger-spec.yaml > target\out\swagger-spec.json

echo "Created the following sample files in .\target\out"
dir .\target\out
