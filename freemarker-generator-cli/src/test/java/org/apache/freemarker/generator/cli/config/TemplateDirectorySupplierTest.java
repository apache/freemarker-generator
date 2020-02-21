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

/**
 * Depending on the configuration the "~/.freemarker-cli" directory
 * might be found.
 */
public class TemplateDirectorySupplierTest {

    private final int nrOfDefaultTemplateDirectories = supplier(null).get().size();

    @Test
    public void shouldRemoveDuplicateTemplateDirectorie() throws IOException {
        final List<File> directories = supplier(".").get();

        assertEquals(nrOfDefaultTemplateDirectories, directories.size());
    }

    @Test
    public void shouldAddTemplateDirectorie() throws IOException {
        assertEquals(nrOfDefaultTemplateDirectories + 1, supplier("templates").get().size());
        assertEquals(nrOfDefaultTemplateDirectories + 1, supplier("./templates").get().size());
    }

    @Test
    public void shouldSkippedNonExistingTemplateDirectorie() throws IOException {
        final List<File> directories = supplier("does-not-exist").get();

        assertEquals(nrOfDefaultTemplateDirectories, directories.size());
    }

    private TemplateDirectorySupplier supplier(String directory) {
        return new TemplateDirectorySupplier(directory);
    }
}
