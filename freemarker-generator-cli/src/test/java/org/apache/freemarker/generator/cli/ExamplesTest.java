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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExamplesTest extends AbstractMainTest {

    private static final int MIN_OUTPUT_SIZE = 5;

    @Test
    public void shouldRunInfo() throws IOException {
        assertValid(execute("-t templates/info.ftl README.md"));
    }

    @Test
    public void shouldRunMultipleTimes() throws IOException {
        assertValid(execute("--times=2 -t templates/info.ftl README.md"));
    }

    @Test
    public void shouldRunDemoExamples() throws IOException {
        assertValid(execute("-t examples/templates/demo.ftl README.md"));
    }

    @Test
    public void shouldRunCsvExamples() throws IOException {
        assertValid(execute("-t examples/templates/csv/html/transform.ftl examples/data/csv/contract.csv"));
        assertValid(execute("-t examples/templates/csv/md/transform.ftl examples/data/csv/contract.csv"));
        assertValid(execute("-t examples/templates/csv/shell/curl.ftl examples/data/csv/user.csv"));
        assertValid(execute("-t examples/templates/csv/fo/transform.ftl examples/data/csv/locker-test-users.csv"));
        assertValid(execute("-t examples/templates/csv/fo/transactions.ftl examples/data/csv/transactions.csv"));
        assertValid(execute("-t examples/templates/csv/html/transactions.ftl examples/data/csv/transactions.csv"));
        assertValid(execute("-t examples/templates/csv/transform.ftl examples/data/csv/contract.csv"));
    }

    @Test
    public void shouldRunExcelExamples() throws IOException {
        assertValid(execute("-t examples/templates/excel/html/transform.ftl examples/data/excel/test.xls"));
        assertValid(execute("-t examples/templates/excel/html/transform.ftl examples/data/excel/test.xlsx"));
        assertValid(execute("-t examples/templates/excel/html/transform.ftl examples/data/excel/test-multiple-sheets.xlsx"));
        assertValid(execute("-t examples/templates/excel/md/transform.ftl examples/data/excel/test-multiple-sheets.xlsx"));
        assertValid(execute("-t examples/templates/excel/csv/transform.ftl examples/data/excel/test-multiple-sheets.xlsx"));
        assertValid(execute("-t examples/templates/excel/csv/custom.ftl -Pcsv.format=MYSQL examples/data/excel/test.xls"));
        assertValid(execute("-t examples/templates/excel/dataframe/transform.ftl examples/data/excel/test.xls"));
    }

    @Test
    public void shouldRunHtmlExamples() throws IOException {
        assertValid(execute("-t examples/templates/html/csv/dependencies.ftl examples/data/html/dependencies.html"));
    }

    @Test
    public void shouldRunJsonExamples() throws IOException {
        assertValid(execute("-t examples/templates/json/csv/swagger-endpoints.ftl examples/data/json/swagger-spec.json"));
        assertValid(execute("-t examples/templates/json/md/github-users.ftl examples/data/json/github-users.json"));
        assertValid(execute("-t examples/templates/json/yaml/transform.ftl examples/data/json/swagger-spec.json"));
    }

    @Test
    public void shouldRunPropertiesExamples() throws IOException {
        assertValid(execute("-t examples/templates/properties/csv/locker-test-users.ftl examples/data/properties"));
    }

    @Test
    public void shouldRunYamlExamples() throws IOException {
        assertValid(execute("-t examples/templates/yaml/txt/transform.ftl examples/data/yaml/customer.yaml"));
        assertValid(execute("-t examples/templates/yaml/json/transform.ftl examples/data/yaml/swagger-spec.yaml"));
    }

    @Test
    public void shouldRunXmlExamples() throws IOException {
        assertValid(execute("-t examples/templates/xml/txt/recipients.ftl examples/data/xml/recipients.xml"));
    }

    @Test
    public void shouldRunGrokExamples() throws IOException {
        assertValid(execute("-t examples/templates/accesslog/combined-access.ftl examples/data/accesslog/combined-access.log"));
    }

    @Test
    public void shouldRunWithExposedEnvironmentVariableExamples() throws IOException {
        assertValid(execute("-m env:/// -t ./src/test/templates/environment.ftl"));
    }

    @Test
    public void shouldRunDataFrameExamples() throws IOException {
        assertValid(execute("-DCSV_TOOL_DELIMITER=SEMICOLON -DCSV_TOOL_HEADERS=true -t examples/templates/dataframe/example.ftl examples/data/csv/dataframe.csv"));
    }

    @Test
    public void shouldRunInteractiveTemplateExamples() throws IOException {
        assertValid(execute("-i ${JsonPathTool.parse(DataSources.first).read(\"$.info.title\")} examples/data/json/swagger-spec.json"));
        assertValid(execute("-i ${XmlTool.parse(DataSources.first)[\"recipients/person[1]/name\"]} examples/data/xml/recipients.xml"));
        assertValid(execute("-i ${JsoupTool.parse(DataSources.first).select(\"a\")[0]} examples/data/html/dependencies.html"));
        assertValid(execute("-i ${GsonTool.toJson(YamlTool.parse(DataSources.get(0)))} examples/data/yaml/swagger-spec.yaml"));
        assertValid(execute("-i ${GsonTool.toJson(yaml)} -m yaml=examples/data/yaml/swagger-spec.yaml"));
        assertValid(execute("-i ${YamlTool.toYaml(GsonTool.parse(DataSources.get(0)))} examples/data/json/swagger-spec.json"));
        assertValid(execute("-i ${YamlTool.toYaml(json)} -m json=examples/data/json/swagger-spec.json"));
        assertValid(execute("-i ${DataFrameTool.print(DataFrameTool.fromMaps(GsonTool.parse(DataSources.get(0))))} examples/data/json/github-users.json"));
    }

    @Test
    public void shouldTransformTemplateDirectory() throws IOException {
        assertTrue(execute("-t examples/data/template").contains("server.name=127.0.0.1"));
        assertTrue(execute("-t examples/data/template -PNGINX_HOSTNAME=my.domain.com").contains("server.name=my.domain.com"));
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
            shouldTransformTemplateDirectory();
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
