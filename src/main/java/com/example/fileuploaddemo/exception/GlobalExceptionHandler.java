package com.example.fileuploaddemo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseException handleNotFound(HttpServletRequest request) {
        ResponseException responseException = new ResponseException();
        responseException.setStatus(HttpStatus.NOT_FOUND.value());
        return responseException;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseException handleUnauthorized(HttpServletRequest request) {
        ResponseException responseException = new ResponseException();
        responseException.setStatus(HttpStatus.UNAUTHORIZED.value());
        return responseException;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(BizException.class)
    public ResponseException handleForbidden(HttpServletRequest reqeust, BizException e) {
        ResponseException responseException = new ResponseException();
        responseException.setCode(e.getCode());
        responseException.setMsg(e.getMessage());
        responseException.setStatus(HttpStatus.FORBIDDEN.value());
        return responseException;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseException handleException(HttpServletRequest request, Exception e) {
        ResponseException responseException = new ResponseException();
        responseException.setMsg(e.getMessage());
        responseException.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return responseException;
    }
}
