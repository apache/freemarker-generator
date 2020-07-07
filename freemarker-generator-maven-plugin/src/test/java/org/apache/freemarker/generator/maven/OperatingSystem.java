package org.apache.freemarker.generator.maven;

import java.util.Locale;

/**
 * Helper class to detect the operting system (mostly Windows).
 *
 * TODO should be moved to "freemarker-generator-base"
 */
public class OperatingSystem {
    private static final String OS = System.getProperty("os.name", "unknown").toLowerCase(Locale.ROOT);

    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isMac() {
        return OS.contains("mac");
    }

    public static boolean isUnix() {
        return OS.contains("nux");
    }
}
