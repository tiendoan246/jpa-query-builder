package com.query.querybuilder.constant;

import java.util.stream.Stream;

public enum LogicOperator {
    OR ("or"),
    AND ("and");

    private String code;

    LogicOperator(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static LogicOperator from(final String code) {
        return Stream.of(LogicOperator.values())
                .filter(targetEnum -> targetEnum.code.equals(code))
                .findFirst()
                .orElse(null);
    }
}
