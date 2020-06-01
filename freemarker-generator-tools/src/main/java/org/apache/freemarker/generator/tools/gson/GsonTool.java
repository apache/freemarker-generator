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
package org.apache.freemarker.generator.tools.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.apache.freemarker.generator.base.datasource.DataSource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JSON processing using <a href="https://github.com/google/gson">Google GSON</a>
 */
public class GsonTool {

    private volatile Gson gson;

    /**
     * Parse a data source containing a JSON object.
     *
     * @param dataSource data source
     * @return map representing the JSON
     */
    public Map<String, Object> toMap(DataSource dataSource) {
        try (JsonReader reader = new JsonReader(new InputStreamReader(dataSource.getUnsafeInputStream()))) {
            return gson().fromJson(reader, objectTypeToken());
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse data source:" + dataSource, e);
        }
    }

    /**
     * Parse a JSON object string.
     *
     * @param json Json string
     * @return map representing the JSON object
     */
    public Map<String, Object> toMap(String json) {
        return gson().fromJson(json, objectTypeToken());
    }

    /**
     * Parse a data source containing a JSON array.
     *
     * @param dataSource data source
     * @return list of maps
     */
    public List<Map<String, Object>> toList(DataSource dataSource) {
        try (JsonReader reader = new JsonReader(new InputStreamReader(dataSource.getUnsafeInputStream()))) {
            return gson().fromJson(reader, listTypeToken());
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse data source:" + dataSource, e);
        }
    }

    /**
     * Parse a JSON string containing a JSON array.
     *
     * @param json Json string
     * @return list of maps
     */
    public List<Map<String, Object>> toList(String json) {
        return gson().fromJson(json, listTypeToken());
    }

    /**
     * Converts to JSON string.
     *
     * @param src source object
     * @return JSON string
     */
    public String toJson(Object src) {
        return gson().toJson(src);
    }

    @Override
    public String toString() {
        return "Process JSON files using GSON (see https://github.com/google/gson)";
    }

    private synchronized Gson gson() {
        if (gson == null) {
            gson = new GsonBuilder().setLenient().setPrettyPrinting().disableHtmlEscaping().create();
        }
        return gson;
    }

    private static Type objectTypeToken() {
        return new TypeToken<Map<String, Object>>() {}.getType();
    }

    private static Type listTypeToken() {
        return new TypeToken<ArrayList<Map<String, Object>>>() {}.getType();
    }
}
