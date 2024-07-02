package com.query.querybuilder.utils;

import com.query.querybuilder.constant.SearchOperator;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class CommonUtils {
    public static String DATE_BETWEEN_EXPRESSION = """
                (a.{KEY} >= '%s' AND a.{KEY} <= '%s')
            """;
    public static String DATE_BETWEEN_ALIAS_EXPRESSION = """
                ({ALIAS}.{KEY} >= '%s' AND {ALIAS}.{KEY} <= '%s')
            """;

    public static Map<SearchOperator, String> sqlOperators = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(SearchOperator.EQ, "="),
            new AbstractMap.SimpleEntry<>(SearchOperator.GT, ">"),
            new AbstractMap.SimpleEntry<>(SearchOperator.GTE, ">="),
            new AbstractMap.SimpleEntry<>(SearchOperator.LT, "<"),
            new AbstractMap.SimpleEntry<>(SearchOperator.LTE, "<="),
            new AbstractMap.SimpleEntry<>(SearchOperator.BETWEEN, "({KEY} >= %s AND {KEY} <= %s)"),
            new AbstractMap.SimpleEntry<>(SearchOperator.CONTAINS, "ILIKE"),
            new AbstractMap.SimpleEntry<>(SearchOperator.IN, "IN"),
            new AbstractMap.SimpleEntry<>(SearchOperator.NEQ, "!="),
            new AbstractMap.SimpleEntry<>(SearchOperator.REG, "LIKE"),
            new AbstractMap.SimpleEntry<>(SearchOperator.JSON_IN, "="),
            new AbstractMap.SimpleEntry<>(SearchOperator.JSON_NOT_IN, "!=")
    );

    public static boolean isValidTimeZone(String timezone) {
        return Set.of(TimeZone.getAvailableIDs()).contains(timezone);
    }
}
