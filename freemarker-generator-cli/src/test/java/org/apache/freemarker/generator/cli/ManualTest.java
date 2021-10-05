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

import java.io.IOException;

/**
 * Invoke freemarker-cli and dump the output for ad-hoc manual testing.
 */
public class ManualTest extends AbstractMainTest {

    // private static final String CMD = "-V";
    private static final String CMD =
            "-t src/app/examples/templates/datasources.ftl readme:documentation=README.md src/main/assembly";
            // "-t src/app/examples/templates/datasources.ftl";

    @Override
    public String execute(String commandLine) throws IOException {
        return super.execute(commandLine);
    }

    public static void main(String[] args) {
        try {
            final String output = new ManualTest().execute(CMD);
            System.out.println(output);
        } catch (IOException e) {
            throw new RuntimeException("Executing the manual test failed: " + CMD, e);
        }
    }
}
