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
package org.apache.freemarker.generator.base;

import java.nio.charset.Charset;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.US;

/**
 * Capture the various constants used within the project.
 */
public class FreeMarkerConstants {

    private FreeMarkerConstants() {
    }

    /** Unknown length for a <code>DataSource</code> */
    public static final int DATASOURCE_UNKNOWN_LENGTH = -1;

    /** Default locale for rendering templates */
    public static final Locale DEFAULT_LOCALE = US;

    /* Default encoding for textual content */
    public static final Charset DEFAULT_CHARSET = UTF_8;

    /* Default group name for data sources */
    public static final String DEFAULT_GROUP = "default";
    
    public static class Configuration {

        private Configuration() {
        }

        /** Prefix to extract tools from 'freemarker-cli.properties' */
        public static final String TOOLS_PREFIX = "freemarker.tools.";

        /** Key for reading the configured locale from 'freemarker-cli.properties' */
        public static final String LOCALE_KEY = "freemarker.configuration.setting.locale";

        /** Prefix to extract FreeMarker configuration settings from 'freemarker-cli.properties' */
        public static final String SETTING_PREFIX = "freemarker.configuration.setting.";
    }

    public static class Location {

        private Location() {
        }

        public static final String BYTES = "bytes";
        public static final String ENVIRONMENT = "env";
        public static final String INTERACTIVE = "interactive";
        public static final String INPUTSTREAM = "inputstream";
        public static final String STDIN = "stdin";
        public static final String SYSTEM = "system";
        public static final String STRING = "string";
        public static final String URL = "url";
    }

    public static class Model {

        private Model() {
        }

        public static final String DATASOURCES = "dataSources";

        public static final String FREEMARKER_CLI_ARGS = "freemarker.cli.args";
        public static final String FREEMARKER_LOCALE = "freemarker.locale";
        public static final String FREEMARKER_WRITER = "freemarker.writer";
        public static final String FREEMARKER_TEMPLATE_DIRECTORIES = "freemarker.template.directories";
        public static final String FREEMARKER_USER_SYSTEM_PROPERTIES = "freemarker.user.system.properties";
        public static final String FREEMARKER_USER_PARAMETERS = "freemarker.user.parameters";
    }
}
