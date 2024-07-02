package com.query.querybuilder.constant;

import java.util.stream.Stream;

public enum OrderBy {
    ASC ("asc"),
    DESC ("desc");

    private String code;

    OrderBy(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static OrderBy from(final String code) {
        return Stream.of(OrderBy.values())
                .filter(targetEnum -> targetEnum.code.equals(code))
                .findFirst()
                .orElse(null);
    }
}
