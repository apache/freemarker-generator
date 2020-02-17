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

    /** Content type for binary data */
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    /** Unknown length for a <code>Document </code> */
    public static final int DOCUMENT_UNKNOWN_LENGTH = -1;

    /** Default locale for rendering templates */
    public static final Locale DEFAULT_LOCALE = US;

    /* Default encoding for textual content */
    public static final Charset DEFAULT_CHARSET = UTF_8;

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
        public static final String INPUTSTREAM = "inputstream";
        public static final String STDIN = "stdin";
        public static final String STRING = "string";
        public static final String URL = "url";
    }

    public static class Model {

        private Model() {
        }

        public static final String DOCUMENTS = "Documents";

        public static final String FREEMARKER_CLI_ARGS = "freemarker.cli.args";
        public static final String FREEMARKER_LOCALE = "freemarker.locale";
        public static final String FREEMARKER_WRITER = "freemarker.writer";
        public static final String FREEMARKER_TEMPLATE_DIRECTORIES = "freemarker.template.directories";
        public static final String FREEMARKER_USER_PROPERTIES = "freemarker.user.properties";
    }
}
