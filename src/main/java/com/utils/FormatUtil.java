package com.utils;

import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class FormatUtil {
    /**
     * 首字母大写
     */
    public static String upFirstChar(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static void addMethodWithBestPosition(TopLevelClass topLevelClass, Method method) {
        addMethodWithBestPosition(method, topLevelClass.getMethods());
    }

    private static void addMethodWithBestPosition(Method method, List<Method> methods) {
        int index = -1;
        for (int i = 0; i < methods.size(); i++) {
            Method m = methods.get(i);
            if (m.getName().equals(method.getName())) {
                if (m.getParameters().size() <= method.getParameters().size()) {
                    index = i + 1;
                } else {
                    index = i;
                }
            } else if (m.getName().startsWith(method.getName())) {
                if (index == -1) {
                    index = i;
                }
            } else if (method.getName().startsWith(m.getName())) {
                index = i + 1;
            }
        }
        if (index == -1 || index >= methods.size()) {
            methods.add(methods.size(), method);
        } else {
            methods.add(index, method);
        }
    }
}
