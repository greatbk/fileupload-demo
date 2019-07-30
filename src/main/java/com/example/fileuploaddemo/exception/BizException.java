package com.example.fileuploaddemo.exception;

import lombok.Data;
import org.springframework.core.NestedCheckedException;

@Data
public class BizException extends NestedCheckedException {

    /**
     * 오류코드
     */
    private String code;

    /**
     * 오류메시지
     */
    private String msg;

    /**
     * 생성자
     * @param msg 오류메시지
     */
    public BizException(String msg) {
        super(msg);
        this.msg = msg;
    }

    /**
     * 생성자
     * @param code 오류코드
     * @param msg 오류메시지
     */
    public BizException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
