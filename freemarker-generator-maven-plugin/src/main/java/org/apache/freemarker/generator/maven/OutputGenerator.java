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

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Knows how to generate an output file given five things:
 * <ul>
 * <li>The latest update time of the <b>pom file(s)</b> for the project</li>
 * <li>The location of the <b>generator file</b></li>
 * <li>The location of the <b>template file</b></li>
 * <li>The location of the <b>output file</b></li>
 * <li>A <b>data model<b> used to fill out the template.</li>
 * </ul>
 * <p>Given these five pieces of information, the generator will generate a new output file, but only if any existing
 * generated file is not newer than the inputs (pom, generator, and template).</p>
 */
class OutputGenerator {
    public final long pomModifiedTimestamp;
    public final Path generatorLocation;
    public final Path templateLocation;
    public final Path outputLocation;
    public final Map<String, Object> dataModel;

    private OutputGenerator(
            long pomModifiedTimestamp,
            Path generatorLocation,
            Path templateLocation,
            Path outputLocation,
            Map<String, Object> dataModel) {
        this.pomModifiedTimestamp = pomModifiedTimestamp;
        this.generatorLocation = generatorLocation;
        this.templateLocation = templateLocation;
        this.outputLocation = outputLocation;
        this.dataModel = dataModel;
    }

    /**
     * Uses a fluent builder to make the code more legible in place.
     * Also allows the output generator to be built from multiple locations in source by passing the builder.
     *
     * @return A new fluent builder for the OutputGenerator class.
     */
    public static OutputGeneratorBuilder builder() {
        return new OutputGeneratorBuilder();
    }

    public static class OutputGeneratorBuilder {
        private long pomModifiedTimestamp = Long.MAX_VALUE;
        private Path generatorLocation = null;
        private Path templateLocation = null;
        private Path outputLocation = null;
        private Map<String, Object> dataModel = null;

        public OutputGeneratorBuilder addPomLastModifiedTimestamp(long pomModifiedTimestamp) {
            this.pomModifiedTimestamp = pomModifiedTimestamp;
            return this;
        }

        public OutputGeneratorBuilder addGeneratorLocation(Path generatorLocation) {
            this.generatorLocation = generatorLocation;
            return this;
        }

        public OutputGeneratorBuilder addTemplateLocation(Path templateLocation) {
            this.templateLocation = templateLocation;
            return this;
        }

        public OutputGeneratorBuilder addOutputLocation(Path outputLocation) {
            this.outputLocation = outputLocation;
            return this;
        }

        public OutputGeneratorBuilder addDataModel(Map<String, Object> dataModel) {
            this.dataModel = dataModel;
            return this;
        }

        public OutputGeneratorBuilder addToDataModel(String key, Object val) {
            if (this.dataModel == null) {
                this.dataModel = new HashMap<>(4);
            }
            this.dataModel.put(key, val);
            return this;
        }

        /**
         * @return A new output generator (which is immutable).
         * @throws IllegalStateException if any of the parts of the OutputGenerator were not set.
         */
        public OutputGenerator create() {
            if (pomModifiedTimestamp == Long.MAX_VALUE) {
                throw new IllegalStateException("Must set the pomModifiedTimestamp");
            }
            if (generatorLocation == null) {
                throw new IllegalStateException("Must set a non-null generatorLocation");
            }
            if (templateLocation == null) {
                throw new IllegalStateException("Must set a non-null templateLocation");
            }
            if (outputLocation == null) {
                throw new IllegalStateException("Must set a non-null outputLocation");
            }
            if (dataModel == null) {
                throw new IllegalStateException("Must set a non-null dataModel");
            }
            return new OutputGenerator(pomModifiedTimestamp, generatorLocation, templateLocation, outputLocation, dataModel);
        }
    }

    /**
     * <p>Generates an output by applying the model to the template.</p>
     * <p>Checks the ages of the inputs against an existing output file to early exit if there is no update.</p>
     *
     * @param config Used to load the template from the template name.
     */
    public void generate(Configuration config) {
        final File outputFile = new File(outputLocation.toFile().getAbsolutePath());
        final File templateFile = templateLocation.toFile();
        final File generatorFile = generatorLocation.toFile();
        if (outputFile.exists()) {
            //early exit only if the output file is newer than all files that contribute to its generation
            if (outputFile.lastModified() > generatorFile.lastModified()
                    && outputFile.lastModified() > templateFile.lastModified()
                    && outputFile.lastModified() > pomModifiedTimestamp) {
                return;
            }
        } else {
            final File parentDir = outputFile.getParentFile();
            if (parentDir.isFile()) {
                throw new RuntimeException("Parent directory of output file is a file: " + parentDir.getAbsoluteFile());
            }
            parentDir.mkdirs();
            if (!parentDir.isDirectory()) {
                throw new RuntimeException("Could not create directory: " + parentDir.getAbsoluteFile());
            }
        }

        final Template template;
        try {
            template = config.getTemplate(templateFile.getName());
        } catch (Throwable t) {
            throw new RuntimeException("Could not read template: " + templateFile.getName(), t);
        }

        try (FileWriter writer = new FileWriter(outputFile)) {
            template.process(dataModel, writer);
        } catch (Throwable t) {
            throw new RuntimeException("Could not process template associated with data file: " + generatorLocation, t);
        }
    }
}
