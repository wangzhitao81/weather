package com.example.weather.enums;

public enum ResultStatus {
	OK(20000, "成功"),
	FAIL(40000, "失败"),
	ERROR(50000, "异常");
    private final int code;
    private final String message;
    ResultStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
    public static ResultStatus getResultEnum(int code) {
        for (ResultStatus type : ResultStatus.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return ERROR;
    }
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
