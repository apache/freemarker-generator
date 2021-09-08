package org.apache.freemarker.generator.cli.config.output;

import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.util.StringUtils;

import java.io.File;

public class DataSourceSeedingOutputMapper {

    private final String template;

    public DataSourceSeedingOutputMapper(String template) {
        this.template = template;
    }

    public File map(File outputDirectory, DataSource dataSource) {
        final String relativeFilePath = dataSource.getRelativeFilePath();
        final String fileName = expand(template, dataSource);

        return (StringUtils.isEmpty(relativeFilePath)) ?
                new File(outputDirectory, fileName) :
                new File(new File(outputDirectory, relativeFilePath), fileName);
    }

    private static String expand(String value, DataSource dataSource) {
        if (StringUtils.isEmpty(value)) {
            return dataSource.getBaseName() + "." + dataSource.getExtension();
        }

        return value.replace("*", dataSource.getBaseName());
    }
}
