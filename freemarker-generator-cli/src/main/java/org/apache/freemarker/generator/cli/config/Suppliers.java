package org.apache.freemarker.generator.cli.config;

import freemarker.cache.TemplateLoader;
import org.apache.freemarker.generator.base.datasource.DatasourcesSupplier;
import org.apache.freemarker.generator.base.file.PropertiesClassPathSupplier;
import org.apache.freemarker.generator.base.file.PropertiesFileSystemSupplier;
import org.apache.freemarker.generator.base.file.PropertiesSupplier;

import java.util.function.Supplier;

/**
 * Convenience methods to create suppliers.
 */
public class Suppliers {

    public static ConfigurationSupplier configurationSupplier(Settings settings) {
        return new ConfigurationSupplier(settings, templateLoaderSupplier(settings));
    }

    public static ConfigurationSupplier configurationSupplier(Settings settings, Supplier<TemplateLoader> templateLoader) {
        return new ConfigurationSupplier(settings, templateLoader);
    }

    public static TemplateDirectorySupplier templateDirectorySupplier(String userDefinedTemplateDir) {
        return new TemplateDirectorySupplier(userDefinedTemplateDir);
    }

    public static TemplateLoaderSupplier templateLoaderSupplier(Settings settings) {
        return new TemplateLoaderSupplier(settings.getTemplateDirectories());
    }

    public static ToolsSupplier toolsSupplier(Settings settings) {
        return new ToolsSupplier(settings.getConfiguration(), settings.toMap());
    }

    public static DatasourcesSupplier datasourcesSupplier(Settings settings) {
        return new DatasourcesSupplier(settings.getDatasources(),
                settings.getInclude(),
                settings.getExclude(),
                settings.getInputEncoding());
    }

    public static PropertiesSupplier propertiesSupplier(String fileName) {
        return new PropertiesSupplier(
                new PropertiesFileSystemSupplier(fileName),
                new PropertiesClassPathSupplier(fileName));
    }
}