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
        assertValid(execute("-t templates/demo.ftl README.md"));
    }

    @Test
    public void shouldRunCsvExamples() throws IOException {
        assertValid(execute("-t templates/csv/html/transform.ftl site/sample/csv/contract.csv"));
        assertValid(execute("-t templates/csv/md/transform.ftl site/sample/csv/contract.csv"));
        assertValid(execute("-t templates/csv/shell/curl.ftl site/sample/csv/user.csv"));
        assertValid(execute("-t templates/csv/fo/transform.ftl site/sample/csv/locker-test-users.csv"));
        assertValid(execute("-t templates/csv/fo/transactions.ftl site/sample/csv/transactions.csv"));
        assertValid(execute("-t templates/csv/html/transactions.ftl site/sample/csv/transactions.csv"));
        assertValid(execute("-t templates/csv/transform.ftl site/sample/csv/contract.csv"));
    }

    @Test
    public void shouldRunExcelExamples() throws IOException {
        assertValid(execute("-t templates/excel/html/transform.ftl site/sample/excel/test.xls"));
        assertValid(execute("-t templates/excel/html/transform.ftl site/sample/excel/test.xlsx"));
        assertValid(execute("-t templates/excel/html/transform.ftl site/sample/excel/test-multiple-sheets.xlsx"));
        assertValid(execute("-t templates/excel/md/transform.ftl site/sample/excel/test-multiple-sheets.xlsx"));
        assertValid(execute("-t templates/excel/csv/transform.ftl site/sample/excel/test-multiple-sheets.xlsx"));
        assertValid(execute("-t templates/excel/csv/custom.ftl -Pcsv.format=MYSQL site/sample/excel/test.xls"));
    }

    @Test
    public void shouldRunHtmlExamples() throws IOException {
        assertValid(execute("-t templates/html/csv/dependencies.ftl site/sample/html/dependencies.html"));
    }

    @Test
    public void shouldRunJsonExamples() throws IOException {
        assertValid(execute("-t templates/json/csv/swagger-endpoints.ftl site/sample/json/swagger-spec.json"));
        assertValid(execute("-t templates/json/md/github-users.ftl site/sample/json/github-users.json"));
        assertValid(execute("-t templates/json/yaml/transform.ftl site/sample/json/swagger-spec.json"));
    }

    @Test
    public void shouldRunPropertiesExamples() throws IOException {
        assertValid(execute("-t templates/properties/csv/locker-test-users.ftl site/sample/properties"));
    }

    @Test
    public void shouldRunYamlExamples() throws IOException {
        assertValid(execute("-t templates/yaml/txt/transform.ftl site/sample/yaml/customer.yaml"));
        assertValid(execute("-t templates/yaml/json/transform.ftl site/sample/yaml/swagger-spec.yaml"));
    }

    @Test
    public void shouldRunXmlExamples() throws IOException {
        assertValid(execute("-t templates/xml/txt/recipients.ftl site/sample/xml/recipients.xml"));
    }

    @Test
    public void shouldRunGrokExamples() throws IOException {
        assertValid(execute("-t templates/accesslog/combined-access.ftl site/sample/accesslog/combined-access.log"));
    }

    @Test
    public void shouldRunWithExposedEnvironmentVariableExamples() throws IOException {
        assertValid(execute("-b ./src/test -E -t templates/environment.ftl"));
    }

    @Test
    public void shouldRunInteractiveTemplateExamples() throws IOException {
        assertValid(execute("-i ${JsonPathTool.parse(DataSources.first).read(\"$.info.title\")} site/sample/json/swagger-spec.json"));
        assertValid(execute("-i ${XmlTool.parse(DataSources.first)[\"recipients/person[1]/name\"]} site/sample/xml/recipients.xml"));
        assertValid(execute("-i ${JsoupTool.parse(DataSources.first).select(\"a\")[0]} site/sample/html/dependencies.html"));
        assertValid(execute("-i ${GsonTool.toJson(YamlTool.parse(DataSources.get(0)))} site/sample/yaml/swagger-spec.yaml"));
        assertValid(execute("-i ${YamlTool.toYaml(GsonTool.parse(DataSources.get(0)))} site/sample/json/swagger-spec.json"));
    }

    @Test
    @Ignore("Manual test to check memory consumption and resource handling")
    public void shouldCloseAllResources() throws IOException {
        for (int i = 0; i < 5000; i++) {
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
