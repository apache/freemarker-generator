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
        if (relativePath.lastIndexOf('/') >= 0) {
            return relativePath.substring(0, relativePath.lastIndexOf("/"));
        } else {
            return "";
        }
    }
}
