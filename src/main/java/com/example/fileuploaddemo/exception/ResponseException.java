package com.example.fileuploaddemo.exception;

import lombok.Data;

@Data
public class ResponseException {

    /**
     * 응답코드
     */
    private int status;

    /**
     * 오류코드
     */
    private String code;

    /**
     * 오류메시지
     */
    private String msg;
}
