package com.query.querybuilder.utils;

public class StringUtils {
    private static String strRegex = "([a-z])([A-Z]+)";

    public static String camelToSnake(String str)
    {
        String replacement = "$1_$2";
        str = str.replaceAll(strRegex, replacement).toLowerCase();
        return str;
    }
}
