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
package org.apache.freemarker.generator.base.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    /**
     * Determines the relative path between a directory and a file within the directory (excluding the file name).
     *
     * @param directory the directory
     * @param file      the file
     * @return relative path or an empty string if no relative path exists
     */
    public static String getRelativePath(File directory, File file) {
        Validate.notNull(directory, "directory is null");
        Validate.notNull(file, "file is null");
        Validate.isTrue(directory.exists(), "directory does not exist");

        final Path filePath = Paths.get(file.toURI()).normalize();
        final Path directoryPath = Paths.get(directory.toURI()).normalize();
        final String relativePath = directoryPath.relativize(filePath).normalize().toString();

        // strip last path segment
        if (relativePath.lastIndexOf(File.separatorChar) >= 0) {
            return relativePath.substring(0, relativePath.lastIndexOf(File.separatorChar));
        } else {
            return "";
        }
    }
}
