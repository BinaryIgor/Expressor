package com.binaryigor.expressor.extension;

import java.io.File;

public class FileExtension {

    public static void deleteDir(File dir) {
        if (!dir.exists()) {
            return;
        }

        var files = dir.listFiles();
        if (files != null) {
            for (var f : files) {
                deleteDir(f);
            }
        }
        if (!dir.delete()) {
            throw new RuntimeException("Can't delete %s dir".formatted(dir));
        }
    }
}
