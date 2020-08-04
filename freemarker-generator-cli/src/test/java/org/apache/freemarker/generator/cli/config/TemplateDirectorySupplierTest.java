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
package org.apache.freemarker.generator.cli.config;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TemplateDirectorySupplierTest {

    // Depending on the local configuration the "~/.freemarker-cli" directory might be found.
    private final int nrOfDefaultTemplateDirectories = templateDirectories(null).size();

    @Test
    public void shouldRemoveDuplicateTemplateDirectorie() throws IOException {
        final List<File> directories = templateDirectories(".");

        assertEquals(nrOfDefaultTemplateDirectories, directories.size());
    }

    @Test
    public void shouldAddTemplateDirectory() throws IOException {
        assertEquals(nrOfDefaultTemplateDirectories + 1, templateDirectories("templates").size());
        assertEquals(nrOfDefaultTemplateDirectories + 1, templateDirectories("./templates").size());
    }

    @Test
    public void shouldSkippedNonExistingTemplateDirectorie() throws IOException {
        final List<File> directories = templateDirectories("does-not-exist");

        assertEquals(nrOfDefaultTemplateDirectories, directories.size());
    }

    private List<File> templateDirectories(String directory) {
        return new TemplateDirectorySupplier(directory).get();
    }
}
