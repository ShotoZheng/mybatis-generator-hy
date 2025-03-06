package com.xml.generator.resolver;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneratorConfigResolver {

    public String generatorDynamicXmlContent() throws Exception {
        Properties properties = new Properties();
        // 使用ClassLoader加载properties配置文件生成对应的输入流
        InputStream in = GeneratorConfigResolver.class.getClassLoader().getResourceAsStream("generator.properties");
        // 使用properties对象加载输入流
        properties.load(in);
        if (in != null) {
            in.close();
        }
        // 读取 XML 文件内容
        Path path = Paths.get("src/main/resources/generator-config.xml");
        byte[] fileBytes = Files.readAllBytes(path);
        String xmlContent = new String(fileBytes);

        // 使用正则表达式匹配占位符并替换为实际值
        Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(xmlContent);
        StringBuilder replacedContent = new StringBuilder();

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String replacement = properties.getProperty(placeholder);
            if (replacement != null) {
                matcher.appendReplacement(replacedContent, replacement);
            }
        }
        matcher.appendTail(replacedContent);
        return replacedContent.toString();
    }

}
