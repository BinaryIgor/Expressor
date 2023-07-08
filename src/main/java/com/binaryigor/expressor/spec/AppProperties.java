package com.binaryigor.expressor.spec;

import java.util.Map;

public record AppProperties(String type,
                            String name,
                            Map<String, Object> db,
                            String packageName) {
}
