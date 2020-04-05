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

import java.util.Arrays;

/**
 * Invoke freemarker-cli and dump the output for ad-hoc manual testing.
 */
public class ManualTest {

    private static final String SPACE = " ";
    // private static final String CMD = "-b ./src/test -t templates/csv/html/transactions.ftl site/sample/csv/transactions.csv";
    // private static final String CMD = "-b ./src/test -l de_AT -DFOO=foo -DBAR=bar -t templates/info.ftl site/sample/csv/transactions.csv";
    // private static final String CMD = "-b ./src/test -DFOO=foo -PBAR=bar -l de -t templates/demo.ftl site/sample/csv/transactions.csv";
    // private static final String CMD = "-b ./src/test -DFOO=foo -PBAR=bar -t templates/demo.ftl site/sample/csv/transactions.csv";
    // private static final String CMD = "-b ./src/test -Dcsv.out.format=TDF -t templates/csv/transform.ftl site/sample/csv/contract.csv";
    // private static final String CMD = "-t templates/excel/csv/transform.ftl -l de_AT site/sample/excel/test.xlsx";
    // private static final String CMD = "-i ${JsonPathTool.parse(DataSources.first).read('$.info.title')} site/sample/json/swagger-spec.json";
    // private static final String CMD = "-i ${XmlTool.parse(DataSources.first)['recipients/person[1]/name']} site/sample/xml/recipients.xml";
    // private static final String CMD = "-i ${JsoupTool.parse(DataSources.first).select('a')[0]} site/sample/html/dependencies.html";
    // private static final String CMD = "-b ./src/test -t templates/properties/csv/locker-test-users.ftl site/sample/properties";
    // private static final String CMD = "-b ./src/test -e UTF-8 -l de_AT -Dcolumn=Order%20ID -Dvalues=226939189,957081544 -Dformat=DEFAULT -Ddelimiter=COMMA -t templates/csv/md/filter.ftl site/sample/csv/sales-records.csv";
    // private static final String CMD = "-E -b ./src/test -t templates/environment.ftl";
    // private static final String CMD = "-b ./src/test -l de_AT -DFOO=foo -DBAR=bar -t templates/info.ftl -d user:admin=site/sample/csv/contract.csv#charset=UTF-16 google:www=https://www.google.com?foo=bar#contenttype=application/json";
    // private static final String CMD = "-b ./src/test -t templates/info.ftl -d :user=site/sample/properties -d contract=site/sample/csv/contract.csv";
    // private static final String CMD = "-b ./src/test -t site/sample/ftl/nginx/nginx.conf.ftl -d env=site/sample/ftl/nginx/nginx.env";
    // private static final String CMD = "-b ./src/test -t templates/info.ftl -d env=site/sample/ftl/nginx/nginx.env";
    private static final String CMD = "-b ./src/test -t templates/json/yaml/transform.ftl site/sample/json/swagger-spec.json";
    // private static final String CMD = "-b ./src/test -t templates/yaml/json/transform.ftl site/sample/yaml/swagger-spec.yaml";

    public static void main(String[] args) {
        Main.execute(toArgs(CMD));
    }

    private static String[] toArgs(String line) {
        // map a "%20" to space to protect system property values containing a space
        return Arrays.stream(line.split(SPACE)).map(s -> s.replace("%20", " ")).toArray(String[]::new);
    }
}
