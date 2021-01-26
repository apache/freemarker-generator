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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JsonPropertiesProvider implements OutputGeneratorPropertiesProvider {
    private final Gson gson;
    private final Type stringObjectMap;
    private final File dataDir;
    private final File templateDir;
    private final File outputDir;

    private JsonPropertiesProvider(File dataDir, File templateDir, File outputDir) {
        this.dataDir = dataDir;
        this.templateDir = templateDir;
        this.outputDir = outputDir;
        this.gson = new GsonBuilder().setLenient().create();
        this.stringObjectMap = new TypeToken<Map<String, Object>>() {}.getType();
    }

    public static JsonPropertiesProvider create(File dataDir, File templateDir, File outputDir) {
        return new JsonPropertiesProvider(dataDir, templateDir, outputDir);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void providePropertiesFromFile(Path path, OutputGenerator.OutputGeneratorBuilder builder) {
        final File jsonDataFile = path.toFile();
        final Map<String, Object> data = parseJson(jsonDataFile);

        Object obj = data.get("dataModel");
        if (obj != null) {
            builder.addDataModel((Map<String, Object>) obj);
        } else {
            builder.addDataModel(new HashMap<>());
        }

        obj = data.get("templateName");
        if (obj == null) {
            throw new RuntimeException("Require json data property not found: templateName");
        }
        builder.addTemplateLocation(templateDir.toPath().resolve(obj.toString()));

        final String dataDirName = dataDir.getAbsolutePath();
        final String jsonFileName = jsonDataFile.getAbsolutePath();
        if (!jsonFileName.startsWith(dataDirName)) {
            throw new IllegalStateException("visitFile() given file not in sourceDirectory: " + jsonDataFile);
        }

        String outputFileName = jsonFileName.substring(dataDirName.length() + 1);
        outputFileName = outputFileName.substring(0, outputFileName.length() - 5);
        final Path outputPath = outputDir.toPath();
        final Path resolved = outputPath.resolve(outputFileName);
        builder.addOutputLocation(resolved);
    }

    private Map<String, Object> parseJson(File jsonDataFile) {
        try (JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(jsonDataFile), UTF_8))) {
            return gson.fromJson(reader, stringObjectMap);
        } catch (Throwable t) {
            throw new RuntimeException("Could not parse json data file: " + jsonDataFile, t);
        }
    }
}
