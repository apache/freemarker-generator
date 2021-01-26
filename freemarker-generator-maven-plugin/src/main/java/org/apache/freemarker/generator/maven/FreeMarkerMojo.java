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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class FreeMarkerMojo extends AbstractMojo {

    /** FreeMarker version string used to build FreeMarker Configuration instance. */
    @Parameter
    private String freeMarkerVersion;

    @Parameter(defaultValue = "src/main/freemarker/generator")
    private File sourceDirectory;

    @Parameter(defaultValue = "src/main/freemarker/generator/template")
    private File templateDirectory;

    @Parameter(defaultValue = "src/main/freemarker/generator/generator")
    private File generatorDirectory;

    @Parameter(defaultValue = "target/generated-sources/freemarker/generator")
    private File outputDirectory;

    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession session;

    @Parameter(defaultValue = "${mojoExecution}", readonly = true)
    private MojoExecution mojo;

    @Override
    public void execute() throws MojoExecutionException {

        if (freeMarkerVersion == null || freeMarkerVersion.isEmpty()) {
            throw new MojoExecutionException("freeMarkerVersion is required");
        }

        if (!generatorDirectory.isDirectory()) {
            throw new MojoExecutionException("Required generator directory does not exist: " + generatorDirectory);
        }

        if (!templateDirectory.isDirectory()) {
            throw new MojoExecutionException("Required template directory does not exist: " + templateDirectory);
        }

        final Configuration configuration = configuration();

        if ("generate-sources".equals(mojo.getLifecyclePhase())) {
            session.getCurrentProject().addCompileSourceRoot(outputDirectory.toString());
        } else if ("generate-test-sources".equals(mojo.getLifecyclePhase())) {
            session.getCurrentProject().addTestCompileSourceRoot(outputDirectory.toString());
        }

        final Map<String, OutputGeneratorPropertiesProvider> extensionToBuilders = new HashMap<>(1);
        extensionToBuilders.put(".json", JsonPropertiesProvider.create(generatorDirectory, templateDirectory, outputDirectory));

        final GeneratingFileVisitor fileVisitor = GeneratingFileVisitor.create(configuration, session, extensionToBuilders);
        try {
            Files.walkFileTree(generatorDirectory.toPath(), fileVisitor);
        } catch (Throwable t) {
            getLog().error("Failed to process files in generator dir: " + generatorDirectory, t);
            throw new MojoExecutionException("Failed to process files in generator dir: " + generatorDirectory);
        }
    }

    private Configuration configuration() {
        return new ConfigurationSupplier(freeMarkerVersion, templateDirectory, sourceDirectory).get();
    }
}
