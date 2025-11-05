package com.lsj.cloud.common.core;


import com.lsj.cloud.common.enums.ResultEnum;
import com.lsj.cloud.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.ServletException;
import javax.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException ex) {
        log.warn("业务异常: code={}, message={}", ex.getCode(), ex.getMsg());
        return Result.fail(ex.getCode(), ex.getMsg());
    }

    /**
     * 参数缺失异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.warn("参数缺失: {}", ex.getMessage());
        String message = String.format("参数'%s'是必需的", ex.getParameterName());
        return Result.fail(ResultEnum.VALIDATE_FAILED.getCode(), message);
    }

    /**
     * 参数校验异常 - ConstraintViolationException
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("参数校验失败: {}", ex.getMessage());
        String message = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));
        return Result.fail(ResultEnum.VALIDATE_FAILED.getCode(), message);
    }

    /**
     * 参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("参数类型不匹配: {}", ex.getMessage());
        String message = String.format("参数'%s'类型错误，期望类型: %s",
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "未知");
        return Result.fail(ResultEnum.VALIDATE_FAILED.getCode(), message);
    }

    /**
     * @RequestBody 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("请求体参数校验失败: {}", ex.getMessage());
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Result.fail(ResultEnum.VALIDATE_FAILED.getCode(), message);
    }

    /**
     * 数据绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException ex) {
        log.warn("数据绑定异常: {}", ex.getMessage());
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("; "));
        return Result.fail(ResultEnum.VALIDATE_FAILED.getCode(), message);
    }

    /**
     * Servlet 异常
     */
    @ExceptionHandler(ServletException.class)
    public Result<?> handleServletException(ServletException ex) {
        log.error("Servlet异常: {}", ex.getMessage(), ex);
        return Result.fail(ResultEnum.SYSTEM_ERROR.getCode(), "请求处理失败");
    }

    /**
     * 其他异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception ex) {
        log.error("系统异常: {}", ex.getMessage(), ex);
        return Result.fail(ResultEnum.SYSTEM_ERROR);
    }
}


