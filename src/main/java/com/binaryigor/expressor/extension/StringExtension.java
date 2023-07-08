package com.binaryigor.expressor.extension;

import java.util.Locale;

public class StringExtension {

    public static String capitalized(String string) {
        if (string.isBlank()) {
            return string;
        }

        var first = string.substring(0, 1);
        var rest = string.substring(1);

        return first.toUpperCase(Locale.ROOT) + rest;
    }
}
