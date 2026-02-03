package com.project.platform.exception;

import com.project.platform.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常拦截
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(value = CustomException.class)
    @ResponseBody
    public ResponseEntity<ResponseVO<?>> handleCustomException(CustomException e) {
        log.error("业务异常：{}", e.getMessage());
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ResponseVO.fail(e.getHttpStatus().value(), e.getMessage()));
    }

    /**
     * 处理参数校验异常 (@Valid)
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ResponseVO<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("参数校验异常：{}", errorMsg);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseVO.fail(HttpStatus.BAD_REQUEST.value(), errorMsg));
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public ResponseEntity<ResponseVO<?>> handleBindException(BindException e) {
        String errorMsg = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("参数绑定异常：{}", errorMsg);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseVO.fail(HttpStatus.BAD_REQUEST.value(), errorMsg));
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResponseEntity<ResponseVO<?>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String errorMsg = String.format("参数 '%s' 类型不正确", e.getName());
        log.error("参数类型不匹配：{}", errorMsg);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseVO.fail(HttpStatus.BAD_REQUEST.value(), errorMsg));
    }

    /**
     * 处理请求体不可读异常
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<ResponseVO<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("请求体不可读：{}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseVO.fail(HttpStatus.BAD_REQUEST.value(), "请求参数格式错误"));
    }

    /**
     * 处理路径不存在异常
     */
    @ExceptionHandler(value = NoHandlerFoundException.class)
    @ResponseBody
    public ResponseEntity<ResponseVO<?>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("路径不存在：{}", e.getRequestURL());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseVO.fail(HttpStatus.NOT_FOUND.value(), "请求路径不存在"));
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<ResponseVO<?>> handleException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseVO.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统内部错误"));
    }
}
