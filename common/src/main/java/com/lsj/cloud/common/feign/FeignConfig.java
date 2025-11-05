package com.lsj.cloud.common.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Feign 全局配置
 */
@Slf4j
@Configuration
public class FeignConfig {

    // 黑名单模式，不传递业务相关头
    private static final Set<String> EXCLUDED_HEADERS = Stream.of(
            "content-length",
            "host",
            "connection",
            "accept-encoding"
    ).collect(Collectors.toSet());

    // 白名单模式，只透传业务相关头
    private static final Set<String> INCLUDED_HEADERS = Stream.of(
            "Authorization",   // 鉴权
            "X-Auth-Token",
            "X-Tenant-Id",     // 租户
            "X-User-Id",       // 用户
            "traceId", "spanId", // 链路追踪
            "X-B3-TraceId", "X-B3-SpanId", "X-B3-Sampled", // zipkin
            "traceparent", "tracestate", // W3C Trace Context
            "Accept-Language" // 多语言
    ).collect(Collectors.toSet());


    /**
     * 自定义解码器
     */
    @Bean
    public Decoder feignDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new OptionalDecoder(
                new ResponseEntityDecoder(
                        new FeignResponseDecoder(new SpringDecoder(messageConverters))
                )
        );
    }

    /**
     * Feign 请求拦截器：日志打印 + 头信息透传
     */
    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return template -> {
            logFeignRequestDetails(template);
            propagateHeaders(template);
        };
    }

    private void logFeignRequestDetails(RequestTemplate template) {
        log.debug("🔍 Feign请求详情:");
        log.debug("  Method: {}", template.method());
        log.debug("  URL: {}", template.url());
        log.debug("  Headers: {}", template.headers());
        if (template.body() != null) {
            log.debug("  Body: {}", new String(template.body(), StandardCharsets.UTF_8));
        }
    }

    private void propagateHeaders(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames != null && headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();

            // 排除不需要传递的头部
            if (shouldPropagateHeader(headerName)) {
                String headerValue = request.getHeader(headerName);
                template.header(headerName, headerValue);
            }
        }
    }

    private boolean shouldPropagateHeader(String headerName) {
        String lowerHeader = headerName.toLowerCase();
        // 排除技术性头部和安全敏感头部
        return !EXCLUDED_HEADERS.contains(lowerHeader) &&
                !lowerHeader.startsWith("sec-") &&
                !lowerHeader.startsWith("proxy-");
    }
}