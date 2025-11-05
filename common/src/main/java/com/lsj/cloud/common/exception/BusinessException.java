package com.lsj.cloud.common.exception;

import com.lsj.cloud.common.enums.ResultEnum;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String code;

    private final String msg;

    public BusinessException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMessage();
    }

    public BusinessException(ResultEnum resultEnum, String customMessage) {
        super(customMessage);
        this.code = resultEnum.getCode();
        this.msg = customMessage;
    }

    public BusinessException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(String code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return String.format("BusinessException [code=%s >>> msg=%s]", code, msg);
    }
}

