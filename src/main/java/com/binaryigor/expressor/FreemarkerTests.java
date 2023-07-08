package com.binaryigor.expressor;

import freemarker.template.Configuration;

import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import java.util.UUID;

public class FreemarkerTests {
    public static void main(String[] args) throws Exception {
        var cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setClassForTemplateLoading(FreemarkerTests.class, "/");

//        var schema = Map.of(
//                "package", "com.binaryigor.user",
//                "imports", List.of("java.util.UUID"),
//                "name", "User",
//                "schema", Map.of("id", "UUID",
//                        "name", "String"));

        var schema = new Schema("com.binaryigor.user",
                List.of(),
                "User",
                Map.of("id", "UUID",
                        "name", "String"));

        var temp = cfg.getTemplate("record.tmpl");

        System.out.println(temp);

        var out = new OutputStreamWriter(System.out);

        temp.process(schema, out);
    }

    public record Schema(String packageName,
                  List<String> imports,
                  String name,
                  Map<String, String> schema) {

    }
}
