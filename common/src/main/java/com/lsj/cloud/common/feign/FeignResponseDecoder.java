package com.lsj.cloud.common.feign;

import com.lsj.cloud.common.core.Result;
import com.lsj.cloud.common.enums.ResultEnum;
import com.lsj.cloud.common.exception.BusinessException;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Slf4j
public class FeignResponseDecoder implements Decoder {

    private final Decoder delegate;

    public FeignResponseDecoder(Decoder decoder) {
        this.delegate = decoder;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        logResponseDetails(response);

        Method method = response.request().requestTemplate().methodMetadata().method();
        Class<?> returnType = method.getReturnType();

        // 如果返回类型不是 Result，需要包装解码
        if (returnType != Result.class) {
            ParameterizedType parameterizedType = createResultType(type);
            Result<?> result = (Result<?>) this.delegate.decode(response, parameterizedType);

            return handleResult(result);
        }

        // 直接返回 Result 类型
        return delegate.decode(response, type);
    }

    /*@Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        log.info("⬅️ Feign 响应: status={}, url={}", response.status(), response.request().url());

        // 如果返回不是包装类，直接用默认解码器
        if (!(type instanceof ParameterizedType)) {
            Object decoded = delegate.decode(response, type);
            log.debug("⬅️ Feign解码对象: {}", decoded);
            return decoded;
        }

        ParameterizedType parameterizedType = (ParameterizedType) type;
        if (parameterizedType.getRawType() == Result.class) {
            // 返回值是 Result<T>
            Result<?> result = (Result<?>) delegate.decode(response, type);
            log.debug("⬅️ Feign调用返回原始Result: {}", result);

            if (result == null) {
                throw new BusinessException(ResultEnum.FAIL.getCode(), "服务调用失败: 返回空结果");
            }
            if (result.isFailed()) {
                // 统一异常处理：可映射为业务异常
                throw new BusinessException(result.getCode(), String.format("远程服务调用失败: %s (错误码: %s)", result.getMessage(), result.getCode()));
            }
            return result.getData();
        }

        // 否则正常解码
        return delegate.decode(response, type);
    }*/

    private Object handleResult(Result<?> result) {
        if (result == null) {
            log.debug("Feign响应结果为null");
            return null;
        }

        if (result.isSuccess()) {
            return result.getData();
        } else {
            throw new BusinessException(result.getCode(), String.format("远程服务调用失败: %s (错误码: %s)", result.getMessage(), result.getCode()));
        }
    }

    private void logResponseDetails(Response response) {
        log.debug("Feign响应状态: {}, URL: {}", response.status(), response.request().url());

        // 记录错误响应
        if (response.status() >= 400) {
            log.debug("Feign请求失败 - 状态码: {}, URL: {}", response.status(), response.request().url());
        }
    }

    private ParameterizedType createResultType(Type type) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{type};
            }

            @Override
            public Type getRawType() {
                return Result.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }
}
