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
    private static final String CMD = "-V";
    // private static final String CMD = "-PCSV_SOURCE_FORMAT=DATAFRAME -t examples/templates/dataframe/example.ftl https://raw.githubusercontent.com/nRo/DataFrame/master/src/test/resources/users.csv";
    // private static final String CMD = "-PCSV_SOURCE_WITH_HEADER=false -PCSV_SOURCE_FORMAT=DEFAULT -PCSV_TARGET_FORMAT=EXCEL -PCSV_TARGET_WITH_HEADER=true -t templates/csv/csv/transform.ftl examples/data/csv/contract.csv";
    // private static final String CMD = "-t examples/templates/json/dataframe/github-users.ftl examples/data/json/github-users.json";


    public static void main(String[] args) {
        Main.execute(toArgs(CMD));
    }

    private static String[] toArgs(String line) {
        // map a "%20" to space to protect system property values containing a space
        return Arrays.stream(line.split(SPACE)).map(s -> s.replace("%20", " ")).toArray(String[]::new);
    }
}
