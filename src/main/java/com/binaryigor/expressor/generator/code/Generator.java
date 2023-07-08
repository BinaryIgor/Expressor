package com.binaryigor.expressor.generator.code;

import java.io.File;
import java.nio.file.Path;

public class Generator {

    public static String[] packageDirs(String packageName) {
        return packageName.split("\\.");
    }

    public static String packageName(Path packagePath) {
        var stringPath = packagePath.toString();
        var prefix = "src/main/java";
        var prefixIdx = stringPath.indexOf(prefix);
        if (prefixIdx < 0) {
            throw new RuntimeException("Invalid package path, it has to contain " + prefix);
        }

        var toReplacePackagePath = stringPath.substring(prefixIdx + prefix.length());
        if (toReplacePackagePath.startsWith(File.separator)) {
            toReplacePackagePath = toReplacePackagePath.substring(1);
        }
        return toReplacePackagePath.replace(File.separator, ".");
    }

    public static String fullPackageName(String rootPackage, String packageName) {
        return rootPackage + "." + packageName;
    }
}
