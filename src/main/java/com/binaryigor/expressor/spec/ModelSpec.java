package com.binaryigor.expressor.spec;

import java.util.Map;

public record ModelSpec(Map<String, String> schema,
                        String module) {
}
