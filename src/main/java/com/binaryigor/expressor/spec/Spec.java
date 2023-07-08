package com.binaryigor.expressor.spec;

import java.util.List;
import java.util.Map;

public record Spec(AppProperties app,
                   List<RuleDefinition> rules,
                   Map<String, List<EndpointDefinition>> endpoints) {
}
