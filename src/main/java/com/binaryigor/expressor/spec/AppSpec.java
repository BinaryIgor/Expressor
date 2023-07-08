package com.binaryigor.expressor.spec;

import java.util.List;

public record AppSpec(Spec spec,
                      List<ModelSpec> models) {
}
