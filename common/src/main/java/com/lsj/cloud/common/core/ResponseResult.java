package com.lsj.cloud.common.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsj.cloud.common.anno.ResponseNotIntercept;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ResponseResult implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (returnType.getDeclaringClass().isAnnotationPresent(ResponseNotIntercept.class)) {
            //若在类中加了@ResponseNotIntercept 则该类中的方法不用做统一的拦截
            return false;
        }
        //若方法上加了@ResponseNotIntercept 则该方法不用做统一的拦截
        return !Objects.requireNonNull(returnType.getMethod()).isAnnotationPresent(ResponseNotIntercept.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        log.info(">>> ResponseResult: {}", body);
        if (body instanceof Result) {
            // 提供一定的灵活度，如果body已经被包装了，就不进行包装
            return body;
        }
        // 如果原始返回值是字符串
        if (body instanceof String) {
            try {
                // 返回JSON字符串，但需要提前包装成Result类型
                return new ObjectMapper().writeValueAsString(Result.success(body));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("[" + body + "]无法转为JSON", e);
            }
        }
        return Result.success(body);
    }
}
