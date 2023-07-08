package com.binaryigor.expressor.spec;

import java.util.Map;

public record ModelSpec(String name,
                        Map<String, String> schema,
                        String module) {
}
