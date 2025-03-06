package com.utils;

import org.mybatis.generator.api.dom.java.*;

public class JavaElementGeneratorUtil {

    public static Field generateField(String fieldName, JavaVisibility visibility, FullyQualifiedJavaType javaType, String initString) {
        Field field = new Field(fieldName, javaType);
        field.setVisibility(visibility);
        if (initString != null) {
            field.setInitializationString(initString);
        }
        return field;
    }

    public static Method generateSetterMethod(Field field) {
        Method method = generateMethod(
                "set" + FormatUtil.upFirstChar(field.getName()),
                JavaVisibility.PUBLIC,
                null,
                new Parameter(field.getType(), field.getName())
        );
        return generateMethodBody(method, "this." + field.getName() + " = " + field.getName() + ";");
    }

    public static Method generateGetterMethod(Field field) {
        Method method = generateMethod(
                "get" + FormatUtil.upFirstChar(field.getName()),
                JavaVisibility.PUBLIC,
                field.getType()
        );
        return generateMethodBody(method, "return this." + field.getName() + ";");
    }

    public static Method generateMethod(String methodName, JavaVisibility visibility, FullyQualifiedJavaType returnType, Parameter... parameters) {
        Method method = new Method(methodName);
        method.setVisibility(visibility);
        method.setReturnType(returnType);
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                method.addParameter(parameter);
            }
        }
        return method;
    }

    public static Method generateMethodBody(Method method, String... bodyLines) {
        if (bodyLines != null) {
            for (String bodyLine : bodyLines) {
                method.addBodyLine(bodyLine);
            }
        }
        return method;
    }
}
