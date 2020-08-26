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
package org.apache.freemarker.generator.cli.config;

import org.apache.commons.io.FilenameUtils;
import org.apache.freemarker.generator.base.FreeMarkerConstants.Configuration;
import org.apache.freemarker.generator.base.FreeMarkerConstants.SystemProperties;
import org.apache.freemarker.generator.base.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.freemarker.generator.base.util.StringUtils.isNotEmpty;

/**
 * Determine a list of directories to load a relative template file in the following order
 * <ol>
 *    <li>User-defined template directory</li>
 *    <li>~/.freemarker-generator</li>
 *    <li>Application installation directory</li>
 * </ol>
 */
public class TemplateDirectorySupplier implements Supplier<List<File>> {

    /** Additional template directory, e.g. provided as command line parameter */
    private final String additionalTemplateDirName;

    public TemplateDirectorySupplier(String additionalTemplateDirName) {
        this.additionalTemplateDirName = additionalTemplateDirName;
    }

    @Override
    public List<File> get() {
        return templateLoaderDirectories().stream()
                .filter(StringUtils::isNotEmpty)
                .map(FilenameUtils::normalize)
                .map(File::new)
                .distinct()
                .filter(TemplateDirectorySupplier::isDirectory)
                .collect(toList());
    }

    private List<String> templateLoaderDirectories() {
        return new ArrayList<>(asList(
                additionalTemplatesDirectory(),
                userConfigTemplatesDirectory(),
                applicationTemplatesDirectory()
        ));
    }

    private String additionalTemplatesDirectory() {
        return isNotEmpty(additionalTemplateDirName) ? new File(additionalTemplateDirName).getAbsolutePath() : null;
    }

    private String userConfigDirectory() {
        final String userHomeDir = System.getProperty(SystemProperties.USER_HOME);
        if (isNotEmpty(userHomeDir)) {
            return new File(userHomeDir, Configuration.USER_CONFIGURATION_DIR_NAME).getAbsolutePath();
        } else {
            return null;
        }
    }

    private String userConfigTemplatesDirectory() {
        return templatesDirectory(userConfigDirectory());
    }

    private static String applicationTemplatesDirectory() {
        final String appHomeDir = System.getProperty(SystemProperties.APP_HOME);
        return templatesDirectory(appHomeDir);
    }

    private static String templatesDirectory(String baseDirName) {
        if (isNotEmpty(baseDirName)) {
            return new File(baseDirName, "templates").getAbsolutePath();
        } else {
            return null;
        }
    }

    private static boolean isDirectory(File directory) {
        return directory != null && directory.exists() && directory.isDirectory() && directory.canRead();
    }
}
