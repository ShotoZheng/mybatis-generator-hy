package com.xml.generator.plugin;

import com.utils.FormatUtil;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

/**
 * Example 文件 sonar 优化插件
 */
public class SonarFormatPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        for (Method method : topLevelClass.getMethods()) {
            replaceCreateCriteriaBodyLines(method);
            replaceCreateCriteriaInternal(method);
        }
        // 处理子类
        List<InnerClass> innerClasses = topLevelClass.getInnerClasses();
        for (InnerClass innerClass : innerClasses) {
            if ("GeneratedCriteria".equals(innerClass.getType().getShortName())) {
                List<Method> methods = innerClass.getMethods();
                for (Method method : methods) {
                    replaceIsValidBodyLines(method);
                    replaceEqualToBodyLines(topLevelClass, method);
                    replaceRuntimeException(method);
                }
            }
        }
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    private void replaceRuntimeException(Method method) {
        if (!"addCriterion".equals(method.getName())) {
            return;
        }
        List<String> bodyLines = method.getBodyLines();
        String exceptionBodyLine = bodyLines.get(1);
        String exceptionMsg = "throw new IllegalArgumentException" + exceptionBodyLine.substring(exceptionBodyLine.indexOf("("));
        bodyLines.remove(1);
        bodyLines.add(1, exceptionMsg);
    }

    private void replaceEqualToBodyLines(TopLevelClass topLevelClass, Method method) {
        String name = method.getName();
        if (!name.endsWith("EqualTo") && !name.endsWith("GreaterThan") && !name.endsWith("LessThan") && !name.endsWith("In")
                && !name.endsWith("Between") && !name.endsWith("Like")) {
            return;
        }
        // 解析 bodyLines
        List<String> bodyLines = method.getBodyLines();
        String firstBodyLine = bodyLines.get(0);
        if (!firstBodyLine.startsWith("addCriterion")) {
            return;
        }
        int s = firstBodyLine.lastIndexOf(",") + 2;
        int e = firstBodyLine.lastIndexOf(")");
        String property = firstBodyLine.substring(s, e);

        Field field = new Field(FormatUtil.camelToSnake(property.substring(1, property.length() - 1)), FullyQualifiedJavaType.getStringInstance());
        // 修改 body
        bodyLines.remove(0);
        bodyLines.add(0, replace(firstBodyLine, s, e, field.getName()));

        // 添加静态字段
        boolean exist = topLevelClass.getFields().stream().anyMatch(exsitField -> exsitField.getName().equals(field.getName()));
        if (exist) {
            return;
        }
        field.setFinal(true);
        field.setStatic(true);
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setInitializationString(property);
        topLevelClass.addField(field);
    }

    public String replace(String firstBodyLine, int s, int e, String property) {
        StringBuilder stringBuffer = new StringBuilder();
        char[] charArray = firstBodyLine.toCharArray();
        boolean flag = false;
        for (int i = 0; i < charArray.length; i++) {
            if (i < s || i >= e) {
                stringBuffer.append(charArray[i]);
                continue;
            }
            if (!flag) {
                stringBuffer.append(property);
                flag = true;
            }
        }
        return stringBuffer.toString();
    }

    private void replaceCreateCriteriaInternal(Method method) {
        if (!"createCriteriaInternal".equals(method.getName())) {
            return;
        }
        List<String> bodyLines = method.getBodyLines();
        bodyLines.clear();
        bodyLines.add("return new Criteria();");
    }

    private void replaceCreateCriteriaBodyLines(Method curMethod) {
        if (!"createCriteria".equals(curMethod.getName())) {
            return;
        }
        List<String> bodyLines = curMethod.getBodyLines();
        bodyLines.add(1, "if (oredCriteria.isEmpty()) {");
        bodyLines.remove(2);
    }

    private void replaceIsValidBodyLines(Method curMethod) {
        if (!"isValid".equals(curMethod.getName())) {
            return;
        }
        curMethod.getBodyLines().clear();
        curMethod.addBodyLine("return !criteria.isEmpty();");
    }

}
