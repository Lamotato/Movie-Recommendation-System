package com.project.platform.exception;

import org.springframework.http.HttpStatus;

/**
 * 资源不存在异常
 */
public class NotFoundException extends CustomException {
    public NotFoundException(String msg) {
        super(HttpStatus.NOT_FOUND, "NOT_FOUND", msg);
    }
}
