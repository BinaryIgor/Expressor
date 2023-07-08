package com.binaryigor.expressor.generator.code;

import com.binaryigor.expressor.spec.ModelSpec;
import freemarker.template.Configuration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class JavaClassGenerator {

    private static final Map<String, String> RECORD_IMPORTS = Map.of("UUID", "java.util.UUID");
    private final Configuration templatesConfig;

    public JavaClassGenerator(Configuration templatesConfig) {
        this.templatesConfig = templatesConfig;
    }

    public void generateRecord(Path rootPackagePath,
                               ModelSpec spec) {
        var packageName = Generator.packageName(rootPackagePath);
        var recordPackage = Generator.fullPackageName(packageName, spec.module());
        var recordDir = Path.of(rootPackagePath.toString(), Generator.packageDirs(spec.module()));

        createDirectoryIfNotExists(recordDir);

        var recordPath = Path.of(recordDir.toString(), spec.name() + ".java");

        try (var out = new OutputStreamWriter(new FileOutputStream(recordPath.toFile()))) {
            var recordTemplate = templatesConfig.getTemplate("record.tmpl");

            var data = Map.of("marker", GeneratorConstants.GENERATED_BY_MARKER,
                    "package", recordPackage,
                    "imports", recordImports(spec.schema()),
                    "name", spec.name(),
                    "schema", spec.schema());

            recordTemplate.process(data, out);
        } catch (Exception e) {
            throw new RuntimeException("Problem while generating record of spec: " + spec, e);
        }
    }

    private void createDirectoryIfNotExists(Path packagePath) {
        try {
            Files.createDirectory(packagePath);
        } catch (Exception e) {
            throw new RuntimeException("Problem while creating directory for package:" + packagePath, e);
        }
    }

    private Collection<String> recordImports(Map<String, String> schema) {
        return schema.values().stream()
                .map(RECORD_IMPORTS::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

}
