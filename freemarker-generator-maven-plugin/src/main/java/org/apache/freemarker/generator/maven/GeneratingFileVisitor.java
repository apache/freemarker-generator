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
import org.apache.maven.execution.MavenSession;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

/**
 * FileVisitor designed to process json data files. The json file parsed into
 * a map and given to FreeMarker to.
 */
public class GeneratingFileVisitor extends SimpleFileVisitor<Path> {

    private final Configuration config;
    private final MavenSession session;
    private final long pomLastModifiedTimestamp;
    private final Map<String, OutputGeneratorPropertiesProvider> extensionToBuilder;

    private GeneratingFileVisitor(Configuration config, MavenSession session, Map<String, OutputGeneratorPropertiesProvider> extensionToBuilder) {
        this.config = config;
        this.session = session;
        this.extensionToBuilder = extensionToBuilder;
        this.pomLastModifiedTimestamp = session.getAllProjects().stream()
                .map(project -> project.getFile().lastModified())
                .reduce(Long::max)
                .orElse(0L);
    }

    /**
     * Factory method that calls constructor, added to facilitate testing with jmockit.
     *
     * @param config             FreeMarker configuration
     * @param session            Maven session
     * @param extensionToBuilder builder extension
     * @return GeneratingFileVisitor instance
     */
    public static GeneratingFileVisitor create(Configuration config, MavenSession session, Map<String, OutputGeneratorPropertiesProvider> extensionToBuilder) {
        return new GeneratingFileVisitor(config, session, extensionToBuilder);
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
        if (attrs.isRegularFile()) {
            final OutputGenerator.OutputGeneratorBuilder builder = OutputGenerator.builder()
                    .addGeneratorLocation(path)
                    .addPomLastModifiedTimestamp(pomLastModifiedTimestamp);
            final String fileName = path.getFileName().toString();
            final String extension = fileName.substring(fileName.lastIndexOf('.'));
            final OutputGeneratorPropertiesProvider pathProcessor = extensionToBuilder.get(extension);
            if (pathProcessor == null) {
                throw new RuntimeException("Unknown file extension: " + path);
            }
            pathProcessor.providePropertiesFromFile(path, builder);
            builder.addToDataModel("session", session);
            builder.addToDataModel("pomProperties", session.getCurrentProject().getProperties());
            builder.create().generate(config);
        }
        return FileVisitResult.CONTINUE;
    }
}
