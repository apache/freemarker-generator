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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * Determine a list of directories to load a relative template file in the following order
 * <ol>
 *    <li>User-defined template directory</li>
 *    <li>Current working directory</li>
 *    <li>~/.freemarker-generator</li>
 *    <li>Application installation directory</li>
 * </ol>
 */
public class TemplateDirectorySupplier implements Supplier<List<File>> {

    /** Installation directory of "freemarker-generator" when invoked with shell wrapper */
    private static final String APP_HOME = "app.home";

    /** Current working directory when invoked with shell wrapper */
    private static final String USER_DIR = "user.dir";

    /** Home directory of the user */
    private static final String USER_HOME = "user.home";

    /** The user's optional "freemarker-generator" directory */
    private static final String USER_CONFIGURATION_DIR_NAME = ".freemarker-generator";

    /** User-defined template directory */
    private final String userDefinedTemplateDir;

    public TemplateDirectorySupplier(String userDefinedTemplateDir) {
        this.userDefinedTemplateDir = userDefinedTemplateDir;
    }

    @Override
    public List<File> get() {
        return templateLoaderDirectories().stream()
                .filter(Objects::nonNull)
                .map(FilenameUtils::normalize)
                .map(File::new)
                .distinct()
                .filter(TemplateDirectorySupplier::isDirectory)
                .collect(toList());
    }

    private List<String> templateLoaderDirectories() {
        return new ArrayList<>(asList(
                userTemplateDirName(),
                currentWorkingDirName(),
                userConfigDirName(),
                applicationDirName()
        ));
    }

    private String userTemplateDirName() {
        return userDefinedTemplateDir != null ? new File(userDefinedTemplateDir).getAbsolutePath() : null;
    }

    private String userConfigDirName() {
        final String userHomeDir = System.getProperty(USER_HOME);
        return new File(userHomeDir, USER_CONFIGURATION_DIR_NAME).getAbsolutePath();
    }

    private static String applicationDirName() {
        return System.getProperty(APP_HOME);
    }

    private static String currentWorkingDirName() {
        return System.getProperty(USER_DIR);
    }

    private static boolean isDirectory(File directory) {
        return directory != null && directory.exists() && directory.isDirectory() && directory.canRead();
    }
}
