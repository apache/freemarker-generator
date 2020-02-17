package org.apache.freemarker.generator.base.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;

public class PropertiesFactory {

    public static Properties create(InputStream is) {
        try {
            final Properties properties = new Properties();
            properties.load(is);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties file", e);
        }
    }

    public static Properties create(String value) {
        try (StringReader reader = new StringReader(value)) {
            final Properties properties = new Properties();
            properties.load(reader);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse properties: " + value, e);
        }
    }
}
