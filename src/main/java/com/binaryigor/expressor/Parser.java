package com.binaryigor.expressor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class Parser {

    public static final ObjectMapper YAML = new ObjectMapper(new YAMLFactory());
    public static final ObjectMapper JSON = new ObjectMapper();

    static {
        YAML.findAndRegisterModules();
        YAML.disable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);

        JSON.findAndRegisterModules();
    }
}
