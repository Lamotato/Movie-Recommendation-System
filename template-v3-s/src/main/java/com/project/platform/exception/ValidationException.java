package com.project.platform.exception;

import org.springframework.http.HttpStatus;

/**
 * @author TOYA
 * @description: 具体业务异常类
 */
// 参数校验异常
public class ValidationException extends CustomException {
    public ValidationException(String msg) {
        super(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", msg);
    }
}