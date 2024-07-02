package com.query.querybuilder.constant;

public enum MessageConstant {

    // Validation message
    DATE_BETWEEN_INVALID_ERROR("DATE_BETWEEN_INVALID", "Invalid amount of input date");

    MessageConstant(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private String code;
    private String message;

    public String getCode() {return code;}
    public String getMessage() {return message;}
}
