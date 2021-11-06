package com.example.weather.entity;

import java.io.Serializable;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.example.weather.enums.ResultStatus;

public class RestResult<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2792692944344602915L;
	private int code = 0;
	private String message;
	private T data;
    private Long timestamp;
    public RestResult() {
        super();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public T getData() {
        return data;
    }
    public RestResult<T> code(int code) {
        this.code = code;
        return this;
    }

    public RestResult<T> message(String message) {
        this.message = message;
        return this;
    }

    public RestResult<T> data(T data) {
        this.data = data;
        return this;
    }
    private static <T> RestResult<T> create(int code,String message, T data) {
    	RestResult<T> result = new RestResult<>();
        if (StringUtils.hasLength(message)) {
            result.message(message);
        }

        result.code(code);

        if (!ObjectUtils.isEmpty(data)) {
            result.data(data);
        }

        return result;
    }
    public static <T> RestResult<T> success(int code, String message, T data) {
        return create(code, message, data);
    }
    public static <T> RestResult<T> success(String message, T data) {
        return success(ResultStatus.OK.getCode(), message, data);
    }

    public static <T> RestResult<T> success(String message) {
        return success(message, null);
    }

    public static <T> RestResult<T> success() {
        return success("操作成功！");
    }
    public static <T> RestResult<T> failure(int code, String message, T data) {
        return create(code, message, data);
    }
    public static <T> RestResult<T> success(ResultStatus resultStatus, T data) {
        return success(resultStatus.getCode(), resultStatus.getMessage(), data);
    }
    public static <T> RestResult<T> failure(ResultStatus resultStatus, T data) {
        return failure(resultStatus.getCode(),resultStatus.getMessage(),  data);
    }

    public static <T> RestResult<T> failure(String message, T data) {
        return failure(ResultStatus.FAIL.getCode(), message, data);
    }

    public static <T> RestResult<T> failure(String message) {
        return failure(message,null);
    }

    public static <T> RestResult<T> failure() {
        return failure("操作失败！");
    }
}
