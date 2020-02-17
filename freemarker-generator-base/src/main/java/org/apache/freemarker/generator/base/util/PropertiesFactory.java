package org.apache.freemarker.generator.base.util;

import java.io.IOException;
import java.io.InputStream;
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
}
