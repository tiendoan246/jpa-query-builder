package com.query.querybuilder.constant;

import java.util.stream.Stream;

public enum SearchOperator {
    EQ ("eq"),
    GT ("gt"),
    GTE ("gte"),
    LT ("lt"),
    LTE ("lte"),
    BETWEEN ("between"),
    CONTAINS ("contains"),
    IN ("in"),
    NEQ("neq"),
    REG("reg"),
    RANGE("range"),
    IS_NULL("isNull"),
    JSON_IN("jsonIn"),
    JSON_NOT_IN("jsonNotIn");

    private String code;

    SearchOperator(String code) {
        this.code = code;
    }

    public static SearchOperator from(final String code) {
        return Stream.of(SearchOperator.values())
                .filter(targetEnum -> targetEnum.code.equals(code))
                .findFirst()
                .orElse(null);
    }
}
