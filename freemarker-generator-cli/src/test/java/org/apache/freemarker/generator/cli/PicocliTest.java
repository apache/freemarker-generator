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

import org.junit.Test;
import picocli.CommandLine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PicocliTest {

    private static final String TEMPLATE = "template.ftl";
    private static final String ANY_FILE = "users.csv";
    private static final String ANY_NAMED_FILE = "users=users.csv";
    private static final String OTHER_FILE = "transctions.csv";
    private static final String ANY_FILE_URI = "file:///users.csv";
    private static final String OTHER_FILE_URI = "file:///transctions.csv";

    @Test
    public void testSinglePositionalParameter() {
        assertEquals(ANY_FILE_URI, parse("-t", TEMPLATE, ANY_FILE_URI).sources.get(0));
        assertNull(ANY_FILE, parse("-t", TEMPLATE, ANY_FILE_URI).dataSources);
    }

    @Test
    public void testMultiplePositionalParameter() {
        assertEquals(ANY_FILE, parse("-t", TEMPLATE, ANY_FILE, OTHER_FILE).sources.get(0));
        assertEquals(OTHER_FILE, parse("-t", TEMPLATE, ANY_FILE, OTHER_FILE).sources.get(1));

        assertEquals(ANY_FILE, parse("-t", TEMPLATE, ANY_FILE, OTHER_FILE_URI).sources.get(0));
        assertEquals(OTHER_FILE_URI, parse("-t", TEMPLATE, ANY_FILE, OTHER_FILE_URI).sources.get(1));

        assertEquals(ANY_FILE_URI, parse("-t", TEMPLATE, ANY_FILE_URI, OTHER_FILE_URI).sources.get(0));
        assertEquals(OTHER_FILE_URI, parse("-t", TEMPLATE, ANY_FILE_URI, OTHER_FILE_URI).sources.get(1));
    }

    @Test
    public void testSingleNamedDatasource() {
        assertEquals(ANY_FILE, parse("-t", TEMPLATE, ANY_FILE).sources.get(0));
        assertEquals(ANY_FILE, parse("-t", TEMPLATE, "-d", ANY_FILE).dataSources.get(0));
        assertEquals(ANY_FILE, parse("-t", TEMPLATE, "--data-source", ANY_FILE).dataSources.get(0));
        assertEquals(ANY_FILE_URI, parse("-t", TEMPLATE, "--data-source", ANY_FILE_URI).dataSources.get(0));
    }

    @Test
    public void testMultipleNamedDatasource() {
        final Main main = parse("-t", TEMPLATE, "-d", ANY_FILE, "--data-source", OTHER_FILE_URI);

        assertEquals(ANY_FILE, main.dataSources.get(0));
        assertEquals(OTHER_FILE_URI, main.dataSources.get(1));
        assertNull(main.sources);
    }

    private static Main parse(String... args) {
        final Main main = new Main();
        new CommandLine(main).parseArgs(args);
        return main;
    }
}
