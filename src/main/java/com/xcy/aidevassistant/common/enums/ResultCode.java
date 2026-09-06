package com.xcy.aidevassistant.common.enums;

public enum ResultCode {

    SUCCESS("0", "success"),
    BAD_REQUEST("400", "bad request"),
    INTERNAL_ERROR("500", "internal server error");

    private final String code;
    private final String message;

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
