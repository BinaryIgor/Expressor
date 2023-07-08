package com.binaryigor.expressor.spec;

import java.util.List;

public record EndpointDefinition(String name,
                                 String method,
                                 int returnCode,
                                 List<String> actions) {
}
