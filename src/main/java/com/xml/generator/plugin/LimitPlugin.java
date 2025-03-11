package com.xml.generator.plugin;

import com.utils.FormatUtil;
import com.utils.JavaElementGeneratorUtil;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

public class LimitPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        PrimitiveTypeWrapper integerWrapper = FullyQualifiedJavaType.getIntInstance().getPrimitiveTypeWrapper();

        // 添加offset和rows字段
        Field offsetField = JavaElementGeneratorUtil.generateField(
                "offset", JavaVisibility.PROTECTED, integerWrapper, null);
        topLevelClass.addField(offsetField);
        Field rowsField = JavaElementGeneratorUtil.generateField(
                "rows", JavaVisibility.PROTECTED, integerWrapper, null);
        topLevelClass.addField(rowsField);

        // 增加getter && setter 方法
        Method mSetOffset = JavaElementGeneratorUtil.generateSetterMethod(offsetField);
        FormatUtil.addMethodWithBestPosition(topLevelClass, mSetOffset);
        Method mGetOffset = JavaElementGeneratorUtil.generateGetterMethod(offsetField);
        FormatUtil.addMethodWithBestPosition(topLevelClass, mGetOffset);

        Method mSetRows = JavaElementGeneratorUtil.generateSetterMethod(rowsField);
        FormatUtil.addMethodWithBestPosition(topLevelClass, mSetRows);
        Method mGetRows = JavaElementGeneratorUtil.generateGetterMethod(rowsField);
        FormatUtil.addMethodWithBestPosition(topLevelClass, mGetRows);

        // 增加 page 方法
        Method setPage = JavaElementGeneratorUtil.generateMethod(
                "page",
                JavaVisibility.PUBLIC,
                topLevelClass.getType(),
                new Parameter(integerWrapper, "page"),
                new Parameter(integerWrapper, "pageSize")
        );
        // 默认首页页号是 1
        setPage = JavaElementGeneratorUtil.generateMethodBody(
                setPage,
                "if (page == null || page < 1) {",
                "throw new IllegalArgumentException(\"page for condition cannot be null or less than 1\");",
                "}",
                "if (pageSize == null || pageSize < 1) {",
                "throw new IllegalArgumentException(\"pageSize for condition cannot be null or less than 1\");",
                "}",
                "this.offset = (page - 1) * pageSize;",
                "this.rows = pageSize;",
                "return this;"
        );
        FormatUtil.addMethodWithBestPosition(topLevelClass, setPage);

        // !!! clear 方法增加 offset 和 rows的清理
        List<Method> methodList = topLevelClass.getMethods();
        for (Method method : methodList) {
            if (method.getName().equals("clear")) {
                method.addBodyLine("rows = null;");
                method.addBodyLine("offset = null;");
            }
        }
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }


    /**
     * selectByExample 下增加 limit 节点
     */
    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        generateLimitElement(element);
        return true;
    }

    /**
     * selectByExampleWithBLOBs 下增加 limit 节点
     */
    @Override
    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        generateLimitElement(element);
        return true;
    }

    private void generateLimitElement(XmlElement element) {
        XmlElement ifLimitNotNullElement = new XmlElement("if");
        ifLimitNotNullElement.addAttribute(new Attribute("test", "rows != null"));

        XmlElement ifOffsetNotNullElement = new XmlElement("if");
        ifOffsetNotNullElement.addAttribute(new Attribute("test", "offset != null"));
        ifOffsetNotNullElement.addElement(new TextElement("limit ${offset}, ${rows}"));
        ifLimitNotNullElement.addElement(ifOffsetNotNullElement);

        XmlElement ifOffsetNullElement = new XmlElement("if");
        ifOffsetNullElement.addAttribute(new Attribute("test", "offset == null"));
        ifOffsetNullElement.addElement(new TextElement("limit ${rows}"));
        ifLimitNotNullElement.addElement(ifOffsetNullElement);

        element.addElement(ifLimitNotNullElement);
    }
}
