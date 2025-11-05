package com.lsj.cloud.common.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lsj.cloud.common.enums.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;

    private String message;

    private T data;

    @JsonIgnore
    public boolean isSuccess() {
        return ResultEnum.SUCCESS.getCode().equals(this.code);
    }

    @JsonIgnore
    public boolean isFailed() {
        return !isSuccess();
    }

    // 成功方法
    public static <T> Result<T> success() {
        return new Result<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultEnum.SUCCESS.getCode(), message, data);
    }

    // 失败方法
    public static <T> Result<T> fail() {
        return new Result<>(ResultEnum.FAIL.getCode(), ResultEnum.FAIL.getMessage(), null);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(ResultEnum.FAIL.getCode(), message, null);
    }

    public static <T> Result<T> fail(IResult errorResult) {
        return new Result<>(errorResult.getCode(), errorResult.getMessage(), null);
    }

    public static <T> Result<T> fail(String code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> of(String code, String message, T data) {
        return new Result<>(code, message, data);
    }

}