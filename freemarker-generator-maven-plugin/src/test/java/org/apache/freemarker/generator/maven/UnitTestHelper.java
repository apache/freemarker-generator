/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.freemarker.generator.maven;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class UnitTestHelper {

    public static Configuration configuration(File testDir) {
        try {
            final Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
            configuration.setDefaultEncoding("UTF-8");
            configuration.setTemplateLoader(new FileTemplateLoader(testDir));
            return configuration;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Freemarker configuration", e);
        }
    }

    public static void checkTestDir(File testDir) {
        if (!testDir.isDirectory()) {
            throw new RuntimeException("Can't find required test data directory. "
                    + "If running test outside of maven, make sure working directory is the project directory. "
                    + "Looking for: " + testDir);
        }

    }

    public static void deleteTestOutputDir(File outputDir) {
        try {
            if (outputDir.exists()) {
                // Recursively delete output from previous run.
                Files.walk(outputDir.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to delete output directory", e);
        }

    }
}
