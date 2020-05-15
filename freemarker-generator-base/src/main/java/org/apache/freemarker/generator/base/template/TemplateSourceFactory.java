package org.apache.freemarker.generator.base.template;

import org.apache.freemarker.generator.base.datasource.DataSource;
import org.apache.freemarker.generator.base.datasource.DataSourceFactory;
import org.apache.freemarker.generator.base.util.UriUtils;

import java.io.File;

public class TemplateSourceFactory {

    public static TemplateSource create(String str) {
        if (isTemplatePath(str)) {
            return TemplateSource.fromPath(str);
        } else {
            try (DataSource dataSource = DataSourceFactory.create(str)) {
                return TemplateSource.fromCode(dataSource.getName(), dataSource.getText());
            }
        }
    }

    private static boolean isTemplatePath(String str) {
        return !UriUtils.isUri(str) && !new File(str).exists();
    }
}
