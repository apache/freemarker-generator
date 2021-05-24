/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.freemarker.generator.cli;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExamplesTest extends AbstractMainTest {

    private static final int MIN_OUTPUT_SIZE = 5;

    @Test
    public void shouldRunInfo() throws IOException {
        assertValid(execute("-t src/app/templates/freemarker-generator/info.ftl README.md"));
        assertValid(execute("-t ./src/app/templates/freemarker-generator/info.ftl README.md"));
        assertValid(execute("-t freemarker-generator/info.ftl README.md"));
        assertValid(execute("-t /freemarker-generator/info.ftl README.md"));
    }

    @Test
    public void shouldRunMultipleTimes() throws IOException {
        assertValid(execute("--times=2 -t freemarker-generator/info.ftl README.md"));
    }

    @Test
    public void shouldRunDemoExamples() throws IOException {
        assertValid(execute("-t src/app/examples/templates/demo.ftl README.md"));
    }

    @Test
    public void shouldRunDataSourceExamples() throws IOException {
        assertValid(execute("-t src/app/examples/templates/datasources.ftl -s :csv=src/app/examples/data/csv"));
    }

    @Test
    public void shouldRunCsvExamples() throws IOException {
        assertValid(execute("-t freemarker-generator/csv/html/transform.ftl src/app/examples/data/csv/contract.csv"));
        assertValid(execute("-t freemarker-generator/csv/md/transform.ftl src/app/examples/data/csv/contract.csv"));
        assertValid(execute("-t src/app/examples/templates/csv/shell/curl.ftl src/app/examples/data/csv/user.csv"));
        assertValid(execute("-t src/app/examples/templates/csv/fo/transform.ftl src/app/examples/data/csv/locker-test-users.csv"));
        assertValid(execute("-t src/app/examples/templates/csv/fo/transactions.ftl src/app/examples/data/csv/transactions.csv"));
        assertValid(execute("-t src/app/examples/templates/csv/html/transactions.ftl src/app/examples/data/csv/transactions.csv"));
        assertValid(execute("-t freemarker-generator/csv/csv/transform.ftl src/app/examples/data/csv/contract.csv"));
    }

    @Test
    public void shouldRunExcelExamples() throws IOException {
        assertValid(execute("-t freemarker-generator/excel/html/transform.ftl src/app/examples/data/excel/test.xls"));
        assertValid(execute("-t freemarker-generator/excel/html/transform.ftl src/app/examples/data/excel/test.xlsx"));
        assertValid(execute("-t freemarker-generator/excel/html/transform.ftl src/app/examples/data/excel/test-multiple-sheets.xlsx"));
        assertValid(execute("-t freemarker-generator/excel/md/transform.ftl src/app/examples/data/excel/test-multiple-sheets.xlsx"));
        assertValid(execute("-t freemarker-generator/excel/csv/transform.ftl src/app/examples/data/excel/test-multiple-sheets.xlsx"));
        assertValid(execute("-t src/app/examples/templates/excel/csv/custom.ftl -Pcsv.format=MYSQL src/app/examples/data/excel/test.xls"));
        assertValid(execute("-t src/app/examples/templates/excel/dataframe/transform.ftl src/app/examples/data/excel/test.xls"));
    }

    @Test
    public void shouldRunHtmlExamples() throws IOException {
        assertValid(execute("-t src/app/examples/templates/html/csv/dependencies.ftl src/app/examples/data/html/dependencies.html"));
    }

    @Test
    public void shouldRunJsonExamples() throws IOException {
        assertValid(execute("-t src/app/examples/templates/json/csv/swagger-endpoints.ftl src/app/examples/data/json/swagger-spec.json"));
        assertValid(execute("-t src/app/examples/templates/json/md/github-users.ftl src/app/examples/data/json/github-users.json"));
        assertValid(execute("-t freemarker-generator/json/yaml/transform.ftl src/app/examples/data/json/swagger-spec.json"));
    }

    @Test
    public void shouldRunPropertiesExamples() throws IOException {
        assertValid(execute("-t src/app/examples/templates/properties/csv/locker-test-users.ftl src/app/examples/data/properties"));
    }

    @Test
    public void shouldRunYamlExamples() throws IOException {
        assertValid(execute("-t src/app/examples/templates/yaml/txt/transform.ftl src/app/examples/data/yaml/customer.yaml"));
        assertValid(execute("-t freemarker-generator/yaml/json/transform.ftl src/app/examples/data/yaml/swagger-spec.yaml"));
    }

    @Test
    public void shouldRunXmlExamples() throws IOException {
        assertValid(execute("-t src/app/examples/templates/xml/txt/recipients.ftl src/app/examples/data/xml/recipients.xml"));
    }

    @Test
    public void shouldRunGrokExamples() throws IOException {
        assertValid(execute("-t src/app/examples/templates/accesslog/combined-access.ftl src/app/examples/data/accesslog/combined-access.log"));
        assertValid(execute("-t src/app/examples/templates/logs/csv/serverlog-to-csv.ftl src/app/examples/data/logs/server.01.log"));
    }

    @Test
    public void shouldRunWithExposedEnvironmentVariableExamples() throws IOException {
        assertValid(execute("-m env:/// -t ./src/test/templates/environment.ftl"));
    }

    @Test
    public void shouldRunDataFrameExamples() throws IOException {
        assertValid(execute("-PCSV_SOURCE_DELIMITER=SEMICOLON -PCSV_SOURCE_WITH_HEADER=true -t src/app/examples/templates/dataframe/example.ftl src/app/examples/data/csv/dataframe.csv"));
    }

    @Test
    public void shouldRunJavaFakerExamples() throws IOException {
        assertValid(execute("-t src/app/examples/templates/javafaker/csv/testdata.ftl"));
    }

    @Test
    public void shouldRunInteractiveTemplateExamples() throws IOException {
        assertValid(execute("-i ${tools.jsonpath.parse(dataSources?values[0]).read(\"$.info.title\")} src/app/examples/data/json/swagger-spec.json"));
        assertValid(execute("-i ${tools.xml.parse(dataSources?values[0])[\"recipients/person[1]/name\"]} src/app/examples/data/xml/recipients.xml"));
        assertValid(execute("-i ${tools.jsoup.parse(dataSources?values[0]).select(\"a\")[0]} src/app/examples/data/html/dependencies.html"));
        assertValid(execute("-i ${tools.gson.toJson(tools.yaml.parse(dataSources?values[0]))} src/app/examples/data/yaml/swagger-spec.yaml"));
        assertValid(execute("-i ${tools.gson.toJson(yaml)} -m yaml=src/app/examples/data/yaml/swagger-spec.yaml"));
        assertValid(execute("-i ${tools.yaml.toYaml(tools.gson.parse(dataSources?values[0]))} src/app/examples/data/json/swagger-spec.json"));
        assertValid(execute("-i ${tools.yaml.toYaml(json)} -m json=src/app/examples/data/json/swagger-spec.json"));
        assertValid(execute("-i ${tools.dataframe.print(tools.dataframe.fromMaps(tools.gson.parse(dataSources?values[0])))} src/app/examples/data/json/github-users.json"));
    }

    @Test
    public void shouldTransformSingleTemplateDirectory() throws IOException {
        assertTrue(execute("-t src/app/examples/data/template").contains("server.name=127.0.0.1"));
        assertTrue(execute("-t src/app/examples/data/template -PNGINX_HOSTNAME=my.domain.com").contains("server.name=my.domain.com"));
    }

    @Test
    public void shouldTransformMultipleTemplateDirectories() throws IOException {
        assertValid(execute("-t src/app/examples/data/template -t src/app/examples/data/template"));
        assertValid(execute("-t src/app/examples/data/template -o target/out/template1 -t src/app/examples/data/template -o target/out/template2"));
    }

    @Test
    public void shouldTransformMultipleTemplates() throws IOException {
        assertValid(execute("-t freemarker-generator/csv/md/transform.ftl -t freemarker-generator/csv/html/transform.ftl src/app/examples/data/csv/contract.csv"));
        assertValid(execute("-t freemarker-generator/csv/md/transform.ftl -o target/contract.md -t freemarker-generator/csv/html/transform.ftl -o target/contract.html src/app/examples/data/csv/contract.csv"));
    }

    @Test
    public void shouldTransformMultipleTemplatesAndDataSources() throws IOException {
        final String output = execute(
                "-t freemarker-generator/yaml/json/transform.ftl -s src/app/examples/data/yaml/swagger-spec.yaml -o swagger-spec.json " +
                        "-t freemarker-generator/yaml/json/transform.ftl -s src/app/examples/data/yaml/customer.yaml -o customer.json");

        assertTrue("Swagger file content is missing", output.contains("This is a sample server Petstore server"));
        assertTrue("Customer data is missing", output.contains("Xyz, DEF Street"));
    }

    @Test
    public void shouldSupportDataSourcesAccessInFTL() throws IOException {
        final String args = "users=src/app/examples/data/json/github-users.json contract=src/app/examples/data/csv/contract.csv";

        // check FreeMarker directives
        assertEquals("true", execute(args + " -i ${dataSources?has_content?c}"));
        assertEquals("2", execute(args + " -i ${dataSources?size}"));

        // check FTL map-style access
        assertEquals("users", execute(args + " -i ${dataSources.users.name}"));
        assertEquals("users", execute(args + " -i ${dataSources[\"users\"].name}"));
        assertEquals("application/json", execute(args + " -i ${dataSources[\"users\"].mimeType}"));
    }

    @Test
    @Ignore("Manual test to check memory consumption and resource handling")
    public void shouldCloseAllResources() throws IOException {
        for (int i = 0; i < 500; i++) {
            shouldRunInfo();
            shouldRunDemoExamples();
            shouldRunCsvExamples();
            shouldRunExcelExamples();
            shouldRunHtmlExamples();
            shouldRunJsonExamples();
            shouldRunPropertiesExamples();
            shouldRunYamlExamples();
            shouldRunXmlExamples();
            shouldRunGrokExamples();
            shouldRunInteractiveTemplateExamples();
            shouldTransformSingleTemplateDirectory();
            shouldTransformMultipleTemplates();
            shouldRunWithExposedEnvironmentVariableExamples();
        }
    }

    private static void assertValid(String output) {
        assertTrue(output.length() > MIN_OUTPUT_SIZE);
        assertFalse(output.contains("Exception"));
        assertFalse(output.contains("FreeMarker template error"));
        assertFalse(output.contains("FTL stack trace"));
    }
}
