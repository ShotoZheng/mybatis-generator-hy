package com.xml.generator.plugin;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;

import java.util.Properties;

public class CommentPlugin implements CommentGenerator {
	@Override
	public void addConfigurationProperties(Properties properties) {

	}

	@Override
	public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
		field.addAnnotation("/**");
		field.addAnnotation(" * " + introspectedColumn.getRemarks());
		field.addAnnotation(" */");
	}

}
