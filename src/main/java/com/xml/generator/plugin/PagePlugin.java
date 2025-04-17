package com.xml.generator.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;
import java.util.Set;

/**
 * 基于现有钱包 Mybatis 分页插件实现的分页组件
 */
public class PagePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        // 导入所需的类
        Set<FullyQualifiedJavaType> importTypes = Set.of(
                new FullyQualifiedJavaType("com.ty.mybatis.paginator.domain.PageList"),
                new FullyQualifiedJavaType("com.ty.mybatis.paginator.domain.PageBounds"));
        interfaze.addImportedTypes(importTypes);

        // 在生成的 Mapper 接口中添加分页方法
        FullyQualifiedJavaType listType = new FullyQualifiedJavaType("PageList");
        listType.addTypeArgument(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        Method newMethod = buildPageMethod(introspectedTable, listType, false);
        interfaze.addMethod(newMethod);

        // 大字段分页方法
        if (includeBLOBColumns(introspectedTable)) {
            interfaze.addMethod(buildPageMethod(introspectedTable, listType, true));
        }
        return true;
    }

    /**
     * 是否有 blob 列
     */
    public boolean includeBLOBColumns(IntrospectedTable introspectedTable) {
        return introspectedTable.getRules().generateRecordWithBLOBsClass() && introspectedTable.hasBLOBColumns();
    }

    private Method buildPageMethod(IntrospectedTable introspectedTable, FullyQualifiedJavaType listType, boolean includeBLOBColumns) {
        Method newMethod = new Method("limitByExample");
        if (includeBLOBColumns) {
            newMethod = new Method("limitByExampleWithBLOBs");
        }
        newMethod.setReturnType(listType);
        newMethod.setAbstract(true);

        // 添加参数
        Parameter exampleParam = new Parameter(new FullyQualifiedJavaType(introspectedTable.getExampleType()), "example");
        exampleParam.addAnnotation("@Param(\"example\")");
        newMethod.addParameter(exampleParam);
        Parameter pageBoundsParam = new Parameter(new FullyQualifiedJavaType("PageBounds"), "pageBounds");
        newMethod.addParameter(pageBoundsParam);
        return newMethod;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement sqlEle = generateSqlEle();
        XmlElement limitEle = generateLimitEle(introspectedTable, false);
        document.getRootElement().addElement(sqlEle);
        document.getRootElement().addElement(limitEle);
        // 大字段分页方法
        if (includeBLOBColumns(introspectedTable)) {
            XmlElement limitBLOBColumnsEle = generateLimitEle(introspectedTable, true);
            document.getRootElement().addElement(limitBLOBColumnsEle);
        }
        return true;
    }

    private XmlElement generateLimitEle(IntrospectedTable introspectedTable, boolean includeBLOBColumns) {
        XmlElement selectEle = new XmlElement("select");
        Attribute selectIdAttr = new Attribute("id", "limitByExample");
        if (includeBLOBColumns) {
            selectIdAttr = new Attribute("id", "limitByExampleWithBLOBs");
        }
        selectEle.addAttribute(selectIdAttr);

        String parameterTypePath = introspectedTable.getExampleType();

        selectEle.addAttribute(new Attribute("parameterType", parameterTypePath));
        Attribute resultMapAttr = new Attribute("resultMap", "BaseResultMap");
        if (includeBLOBColumns) {
            resultMapAttr = new Attribute("resultMap", "ResultMapWithBLOBs");
        }
        selectEle.addAttribute(resultMapAttr);
        // select 文本
        selectEle.addElement(new TextElement("select"));

        // if 节点
        XmlElement if1Ele = new XmlElement("if");
        if1Ele.addAttribute(new Attribute("test", "example.distinct"));
        if1Ele.addElement(new TextElement("distinct"));
        selectEle.addElement(if1Ele);

        // include 节点
        XmlElement includeEle = new XmlElement("include");
        includeEle.addAttribute(new Attribute("refid", "Base_Column_List"));
        selectEle.addElement(includeEle);

        // blob 额外字段
        if (includeBLOBColumns) {
            selectEle.addElement(new TextElement(","));
            XmlElement includeBlobEle = new XmlElement("include");
            includeBlobEle.addAttribute(new Attribute("refid", "Blob_Column_List"));
            selectEle.addElement(includeBlobEle);
        }

        // from 文本
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        selectEle.addElement(new TextElement("from " + tableName));

        // if 节点
        XmlElement if2Ele = new XmlElement("if");
        if2Ele.addAttribute(new Attribute("test", "_parameter != null"));

        // include 节点
        XmlElement subIncludeEle = new XmlElement("include");
        subIncludeEle.addAttribute(new Attribute("refid", "Limit_Example_Where_Clause"));
        if2Ele.addElement(subIncludeEle);

        selectEle.addElement(if2Ele);

        // if 节点
        XmlElement if3Ele = new XmlElement("if");
        if3Ele.addAttribute(new Attribute("test", "example.orderByClause != null"));
        if3Ele.addElement(new TextElement("${example.orderByClause}"));
        selectEle.addElement(if3Ele);
        return selectEle;
    }

    private XmlElement generateSqlEle() {
        XmlElement sqlEle = new XmlElement("sql");
        sqlEle.addAttribute(new Attribute("id", "Limit_Example_Where_Clause"));
        // where 节点
        XmlElement whereEle = new XmlElement("where");
        sqlEle.addElement(whereEle);
        // foreach 节点
        XmlElement foreachEle = new XmlElement("foreach");
        foreachEle.addAttribute(new Attribute("collection", "example.oredCriteria"));
        foreachEle.addAttribute(new Attribute("item", "criteria"));
        foreachEle.addAttribute(new Attribute("separator", "or"));
        whereEle.addElement(foreachEle);

        // if 节点
        XmlElement ifEle = new XmlElement("if");
        ifEle.addAttribute(new Attribute("test", "criteria.valid"));
        foreachEle.addElement(ifEle);

        // trim 节点
        XmlElement trimEle = new XmlElement("trim");
        trimEle.addAttribute(new Attribute("prefix", "("));
        trimEle.addAttribute(new Attribute("prefixOverrides", "and"));
        trimEle.addAttribute(new Attribute("suffix", ")"));
        ifEle.addElement(trimEle);

        // foreach 节点
        XmlElement subForeachEle = new XmlElement("foreach");
        subForeachEle.addAttribute(new Attribute("collection", "criteria.criteria"));
        subForeachEle.addAttribute(new Attribute("item", "criterion"));
        trimEle.addElement(subForeachEle);

        // choose 节点
        XmlElement chooseEle = new XmlElement("choose");
        subForeachEle.addElement(chooseEle);

        // when 节点
        XmlElement when1Ele = new XmlElement("when");
        when1Ele.addAttribute(new Attribute("test", "criterion.noValue"));
        when1Ele.addElement(new TextElement("and ${criterion.condition}"));
        chooseEle.addElement(when1Ele);

        XmlElement when2Ele = new XmlElement("when");
        when2Ele.addAttribute(new Attribute("test", "criterion.singleValue"));
        when2Ele.addElement(new TextElement("and ${criterion.condition} #{criterion.value}"));
        chooseEle.addElement(when2Ele);

        XmlElement when3Ele = new XmlElement("when");
        when3Ele.addAttribute(new Attribute("test", "criterion.betweenValue"));
        when3Ele.addElement(new TextElement("and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}"));
        chooseEle.addElement(when3Ele);

        XmlElement when4Ele = new XmlElement("when");
        when4Ele.addAttribute(new Attribute("test", "criterion.listValue"));
        when4Ele.addElement(new TextElement("and ${criterion.condition}"));
        // foreach 节点
        XmlElement whenSubForeachEle = new XmlElement("foreach");
        whenSubForeachEle.addAttribute(new Attribute("close", ")"));
        whenSubForeachEle.addAttribute(new Attribute("collection", "criterion.value"));
        whenSubForeachEle.addAttribute(new Attribute("item", "listItem"));
        whenSubForeachEle.addAttribute(new Attribute("open", "("));
        whenSubForeachEle.addAttribute(new Attribute("separator", ","));
        whenSubForeachEle.addElement(new TextElement("#{listItem}"));
        when4Ele.addElement(whenSubForeachEle);
        chooseEle.addElement(when4Ele);
        return sqlEle;
    }
}
