package com.binaryigor.expressor;

import com.binaryigor.expressor.extension.PathExtension;
import com.binaryigor.expressor.generator.spring.SpringAppGenerator;
import com.binaryigor.expressor.generator.spring.SpringTemplates;
import com.binaryigor.expressor.spec.AppSpec;
import com.binaryigor.expressor.spec.ModelSpec;
import com.binaryigor.expressor.spec.Spec;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new RuntimeException("First argument with the path to spec is required!");
        }

        var specDir = new File(args[0]);
        if (!specDir.exists()) {
            throw new RuntimeException("AppSpec path %s doesn't exist".formatted(specDir));
        }

        var specFile = Paths.get(specDir.getAbsolutePath(), "spec.yml").toFile();
        if (!specFile.exists()) {
            throw new RuntimeException("AppSpec file %s doesn't exist".formatted(specFile));
        }

        System.out.printf("Generating app from file: %s\n", specFile);

        var spec = Parser.YAML.readValue(specFile, Spec.class);
        var modelsSpec = modelsSpec(specDir);
        var appSpec = new AppSpec(spec, modelsSpec);

        System.out.println("Spec read: " + appSpec);

        var templatesDir = PathExtension.templatesUpFromCurrentPath();

        var generator = new SpringAppGenerator(springTemplates(templatesDir), new File(".app"));

        generator.generate(appSpec);
    }

    private static SpringTemplates springTemplates(String templatesDir) throws Exception {
        return new SpringTemplates(Files.readString(Path.of(templatesDir, "spring", "pom.xml")),
                Files.readString(Path.of(templatesDir, "spring", "App.java")));
    }

    private static List<ModelSpec> modelsSpec(File specDir) throws Exception {
        var modelsDir = Path.of(specDir.getAbsolutePath(), "models").toFile();
        if (modelsDir.exists()) {
            var models = new ArrayList<ModelSpec>();
            for (var f : Objects.requireNonNull(modelsDir.listFiles())) {
                var modelSpec = Parser.JSON.readValue(f, ModelSpec.class);
                models.add(modelSpec);
            }
            return models;
        }

        return List.of();
    }
}