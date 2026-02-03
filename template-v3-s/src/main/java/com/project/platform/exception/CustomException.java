package com.project.platform.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 自定义业务异常基类
 */
@Getter
public class CustomException extends RuntimeException {

    private HttpStatus httpStatus;
    private String errorCode;

    public CustomException(HttpStatus httpStatus, String errorCode, String msg) {
        super(msg);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public CustomException(HttpStatus httpStatus, String msg) {
        this(httpStatus, null, msg);
    }

    public CustomException(String msg) {
        this(HttpStatus.CONFLICT, null, msg);
    }

    public CustomException(String errorCode, String msg) {
        this(HttpStatus.CONFLICT, errorCode, msg);
    }
}
