package com.example.fileuploaddemo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseException handleConflict(HttpServletRequest req) {
        ResponseException responseException = new ResponseException();
        responseException.setStatus(HttpStatus.CONFLICT.value());
        return responseException;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseException handleNotFound(HttpServletRequest req) {
        ResponseException responseException = new ResponseException();
        responseException.setStatus(HttpStatus.NOT_FOUND.value());
        return responseException;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BizException.class)
    public ResponseException handleUnauthorized(HttpServletRequest req, BizException e) {
        ResponseException responseException = new ResponseException();
        responseException.setCode(e.getCode());
        responseException.setMsg(e.getMessage());
        responseException.setStatus(HttpStatus.UNAUTHORIZED.value());
        return responseException;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(BizException.class)
    public ResponseException handleForbidden(HttpServletRequest req, BizException e) {
        ResponseException responseException = new ResponseException();
        responseException.setCode(e.getCode());
        responseException.setMsg(e.getMessage());
        responseException.setStatus(HttpStatus.FORBIDDEN.value());
        return responseException;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseException handleException(HttpServletRequest req, Exception e) {
        ResponseException responseException = new ResponseException();
        responseException.setMsg(e.getMessage());
        responseException.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return responseException;
    }
}
