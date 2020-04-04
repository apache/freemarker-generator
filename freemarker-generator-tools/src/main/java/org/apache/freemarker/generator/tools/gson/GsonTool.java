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
import java.util.Map;

public class GsonTool {

    private Gson gson;
    private Type type;

    public Map<String, Object> parse(DataSource dataSource) throws IOException {
        try (JsonReader reader = new JsonReader(new InputStreamReader(dataSource.getUnsafeInputStream()))) {
            return gson().fromJson(reader, type());
        }
    }

    public Map<String, Object> parse(String json) {
        return gson().fromJson(json, type());
    }

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

    private synchronized Type type() {
        if (type == null) {
            type = new TypeToken<Map<String, Object>>() {}.getType();
        }
        return type;
    }
}
