package com.binaryigor.expressor.generator.spring;

import com.binaryigor.expressor.extension.FileExtension;
import com.binaryigor.expressor.extension.StringExtension;
import com.binaryigor.expressor.generator.AppGenerator;
import com.binaryigor.expressor.generator.Templates;
import com.binaryigor.expressor.generator.code.JavaClassGenerator;
import com.binaryigor.expressor.spec.AppProperties;
import com.binaryigor.expressor.spec.AppSpec;
import com.binaryigor.expressor.spec.EndpointDefinition;
import com.binaryigor.expressor.spec.ModelSpec;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class SpringAppGenerator implements AppGenerator {

    public static final String DEFAULT_PACKAGE = "com.example";
    public static final String DEFAULT_VERSION = "0.0.1-SNAPSHOT";
    private final AppTemplates templates;
    private final JavaClassGenerator generator;
    private final File output;

    public SpringAppGenerator(AppTemplates templates,
                              JavaClassGenerator generator,
                              File output) {
        this.templates = templates;
        this.generator = generator;
        this.output = output;
    }

    @Override
    public void generate(AppSpec appSpec) {
        try {
            initOutputDir();

            var spec = appSpec.spec();

            var packageName = packageName(spec.app());
            var appName = spec.app().name();

            preparePom(packageName, appName);

            var mainDirs = prepareSrcMainDirectory(packageName);

            prepareApplicationYml(mainDirs.resources, appName);
            prepareMainClass(mainDirs.java, packageName, appName);

            var testDirs = prepareSrcTestDirectory(packageName);
            prepareBaseTestClass(testDirs.java, packageName);

            prepareModels(mainDirs.java, appSpec.models());

            prepareEndpoints(mainDirs.java, spec.endpoints());
        } catch (Exception e) {
            throw new RuntimeException("Problem while generating spring app!", e);
        }
    }

    private void initOutputDir() throws Exception {
        FileExtension.deleteDir(output);
        Files.createDirectory(output.toPath());
    }

    private String packageName(AppProperties app) {
        return app.packageName() == null ? DEFAULT_PACKAGE : app.packageName();
    }

    private MainDirs prepareSrcMainDirectory(String packageName) throws Exception {
        var mainPath = Path.of(output.getAbsolutePath(), "src", "main");
        Files.createDirectories(mainPath);

        var javaDir = Path.of(Path.of(mainPath.toString(), "java").toString(), packageDirs(packageName));
        Files.createDirectories(javaDir);

        var resourcesDir = Path.of(mainPath.toString(), "resources");
        Files.createDirectories(resourcesDir);

        return new MainDirs(javaDir, resourcesDir);
    }

    private String[] packageDirs(String packageName) {
        return packageName.split("\\.");
    }

    private MainDirs prepareSrcTestDirectory(String packageName) throws Exception {
        var mainPath = Path.of(output.getAbsolutePath(), "src", "test");
        Files.createDirectories(mainPath);

        var javaDir = Path.of(Path.of(mainPath.toString(), "java").toString(), packageDirs(packageName));
        Files.createDirectories(javaDir);

        var resourcesDir = Path.of(mainPath.toString(), "resources");
        Files.createDirectories(resourcesDir);

        return new MainDirs(javaDir, resourcesDir);
    }

    private void prepareBaseTestClass(Path rootTestPackagePath,
                                      String packageName) throws Exception {
        var mainClass = Templates.rendered(templates.baseTest(),
                Map.of("marker", GENERATED_BY_MARKER,
                        "package", packageName));

        Files.writeString(Path.of(rootTestPackagePath.toString(), "IntegrationTest.java"), mainClass);
    }

    private void prepareMainClass(Path rootPackagePath,
                                  String packageName,
                                  String appName) throws Exception {
        var appClassName = StringExtension.capitalized(appName) + "App";

        var mainClass = Templates.rendered(templates.appMain(),
                Map.of("marker", GENERATED_BY_MARKER,
                        "package", packageName,
                        "appClassName", appClassName));

        Files.writeString(Path.of(rootPackagePath.toString(), appClassName + ".java"), mainClass);
    }

    private void preparePom(String packageName,
                            String appName) throws Exception {
        var pom = Templates.rendered(templates.pomXml(),
                Map.of("groupId", packageName,
                        "artifactId", appName,
                        "name", appName,
                        "version", DEFAULT_VERSION));

        Files.writeString(Path.of(output.toString(), "pom.xml"), pom);
    }

    private void prepareApplicationYml(Path resourcesPath,
                                       String appName) throws Exception {
        var yml = """
                # %s
                spring:
                  application:
                    name: %s
                server:
                  port: 8080
                                
                management:
                  endpoints:
                    web:
                      exposure:
                        include: [ "health", "info", "prometheus" ]
                                
                #See docs: https://springdoc.org/v2/
                springdoc:
                  api-docs:
                    enabled: true
                    path: /swagger/v3/api-docs/swagger-config
                  swagger-ui:
                    enabled: true
                    tagsSorter: alpha
                  default-produces-media-type: application/json
                """.formatted(GENERATED_BY_MARKER, appName)
                .strip();

        Files.writeString(Path.of(resourcesPath.toString(), "application.yml"), yml);
    }

    private void prepareModels(Path rootPackagePath,
                               List<ModelSpec> models) {
        for (var m : models) {
            generator.generateRecord(rootPackagePath, m);
        }
    }

    private void prepareEndpoints(Path rootPackagePath,
                                  Map<String, List<EndpointDefinition>> endpoints) {

    }

    private record MainDirs(Path java, Path resources) {
    }
}
