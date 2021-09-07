package org.apache.freemarker.generator.cli.util;

import org.apache.freemarker.generator.base.FreeMarkerConstants.Location;
import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceLoader;
import org.apache.freemarker.generator.base.datasource.DataSourceLoaderFactory;
import org.apache.freemarker.generator.base.template.TemplateSource;
import org.apache.freemarker.generator.base.util.Validate;
import org.apache.freemarker.generator.cli.picocli.TemplateSourceDefinition;

import java.io.File;
import java.nio.charset.Charset;

public class TemplateSourceFactory {

    public static TemplateSource create(TemplateSourceDefinition templateSourceDefinition, Charset charset) {
        Validate.notNull(templateSourceDefinition, "templateSourceDefinition must not be null");
        Validate.notNull(charset, "charset must not be null");

        final String template = templateSourceDefinition.template;
        final DataSourceLoader dataSourceLoader = DataSourceLoaderFactory.create();

        if (templateSourceDefinition.isInteractiveTemplate()) {
            return TemplateSource.fromCode(Location.INTERACTIVE, templateSourceDefinition.interactiveTemplate);
        } else if (new File(template).exists()) {
            final String templateSource = templateSourceDefinition.template;
            try (DataSource dataSource = dataSourceLoader.load(templateSource)) {
                return TemplateSource.fromCode(dataSource.getName(), dataSource.getText(charset.name()));
            }
        } else {
            return TemplateSource.fromPath(template, charset);
        }
    }
}
