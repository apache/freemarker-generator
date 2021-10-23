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
package org.apache.freemarker.generator.tools.utahparser;

import com.sonalake.utah.Parser;
import com.sonalake.utah.config.Config;
import com.sonalake.utah.config.ConfigLoader;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceLoaderFactory;
import org.apache.freemarker.generator.tools.utahparser.impl.ParserWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UtahParserTool {

    /**
     * Create a Utah Parser config based on the given source.
     *
     * @param source source of configuration file
     * @return config instance
     */
    public Config getConfig(String source) {
        return getConfig(DataSourceLoaderFactory.create().load(source));
    }

    /**
     * Create a Utah Parser config based on teh textual content of the data source,
     *
     * @param dataSource XML configuration file
     * @return config instance
     */
    public Config getConfig(DataSource dataSource) {
        try (InputStream is = dataSource.getUnsafeInputStream()) {
            return loadConfig(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load parser configuration: " + dataSource, e);
        }
    }

    /**
     * Create a parser instance.
     *
     * @param config     configuration
     * @param dataSource data source to be parsed
     * @return parser
     */
    public ParserWrapper getParser(Config config, DataSource dataSource) {
        final InputStreamReader is = new InputStreamReader(dataSource.getInputStream());
        final Parser parser = Parser.parse(config, is);
        return new ParserWrapper(parser);
    }

    public List<String> getHeaders(Map<String, Object> record) {
        if (record == null || record.isEmpty()) {
            return Collections.emptyList();
        }

        return new ArrayList<>(record.keySet());
    }

    public List<String> getHeaders(Collection<Map<String, String>> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }

        return records.stream()
                .map(Map::keySet)
                .flatMap(Collection::stream).distinct().sorted().collect(Collectors.toList());
    }

    public String toString() {
        return "Parse semi-structured text using regular expressions (see https://github.com/sonalake/utah-parser)";
    }

    private static Config loadConfig(InputStream is) throws IOException {
        return new ConfigLoader().loadConfig(new InputStreamReader(is));
    }
}