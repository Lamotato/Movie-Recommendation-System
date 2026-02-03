package com.project.platform.exception;

import org.springframework.http.HttpStatus;

/**
 * 未授权异常
 */
public class UnauthorizedException extends CustomException {
    public UnauthorizedException(String msg) {
        super(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", msg);
    }
}
