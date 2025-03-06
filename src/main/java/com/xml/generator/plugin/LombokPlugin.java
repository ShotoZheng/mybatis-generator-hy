package com.xml.generator.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class LombokPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("lombok.Data"); // 添加domain的注解
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addImportedType("lombok.Builder"); // 添加domain的注解
        topLevelClass.addAnnotation("@Builder");
        topLevelClass.addImportedType("lombok.AllArgsConstructor"); // 添加domain的注解
        topLevelClass.addAnnotation("@AllArgsConstructor");
        topLevelClass.addImportedType("lombok.NoArgsConstructor"); // 添加domain的注解
        topLevelClass.addAnnotation("@NoArgsConstructor");
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("lombok.EqualsAndHashCode");
        topLevelClass.addAnnotation("@EqualsAndHashCode(callSuper = true)");
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addAnnotation("@Data");
        return super.modelRecordWithBLOBsClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

}