package com.xml.generator;

import com.xml.generator.resolver.GeneratorConfigResolver;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        generateCode();
    }

    public static void generateCode() throws Exception {
        List<String> warnings = new ArrayList<>();
        boolean overwrite = true;
        GeneratorConfigResolver generatorConfigResolver = new GeneratorConfigResolver();
        byte[] fileBytes = generatorConfigResolver.generatorDynamicXmlContent().getBytes(StandardCharsets.UTF_8);
        Path tempFile = Files.createTempFile("generator-config", ".xml");
        Files.write(tempFile, fileBytes);
        File configFile = tempFile.toFile();
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }
}