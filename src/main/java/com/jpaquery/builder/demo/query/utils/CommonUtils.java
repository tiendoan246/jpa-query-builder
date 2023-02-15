package com.jpaquery.builder.demo.query.utils;

import com.jpaquery.builder.demo.query.constant.SearchOperator;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class CommonUtils {
    public static String DATE_BETWEEN_EXPRESSION = """
                (date_trunc('month', a.{KEY} at time zone '{TIME_ZONE}') >= '%s'  
                AND date_trunc('month', a.{KEY} at time zone '{TIME_ZONE}') <= '%s')
            """;
    public static String DATE_BETWEEN_ALIAS_EXPRESSION = """
                (date_trunc('month', {ALIAS}.{KEY} at time zone '{TIME_ZONE}') >= '%s'  
                AND date_trunc('month', {ALIAS}.{KEY} at time zone '{TIME_ZONE}') <= '%s')
            """;

    public static Map<SearchOperator, String> sqlOperators = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(SearchOperator.EQ, "="),
            new AbstractMap.SimpleEntry<>(SearchOperator.GT, ">"),
            new AbstractMap.SimpleEntry<>(SearchOperator.GTE, ">="),
            new AbstractMap.SimpleEntry<>(SearchOperator.LT, "<"),
            new AbstractMap.SimpleEntry<>(SearchOperator.LTE, "<="),
            new AbstractMap.SimpleEntry<>(SearchOperator.BETWEEN, "({KEY} >= %s AND {KEY} <= %s)"),
            new AbstractMap.SimpleEntry<>(SearchOperator.CONTAINS, "ILIKE"),
            new AbstractMap.SimpleEntry<>(SearchOperator.IN, "IN")
    );

    public static boolean isValidTimeZone(String timezone) {
        return Set.of(TimeZone.getAvailableIDs()).contains(timezone);
    }
}
