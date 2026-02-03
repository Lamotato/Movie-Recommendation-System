package com.project.platform.exception;

import org.springframework.http.HttpStatus;

/**
 * 业务逻辑异常
 */
public class BusinessException extends CustomException {
    public BusinessException(String msg) {
        super(HttpStatus.CONFLICT, "BUSINESS_ERROR", msg);
    }
}
