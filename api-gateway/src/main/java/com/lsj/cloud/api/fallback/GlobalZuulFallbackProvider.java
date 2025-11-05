package com.lsj.cloud.api.fallback;

import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class GlobalZuulFallbackProvider implements FallbackProvider {

    /**
     * 针对哪个服务生效：
     *  - 返回具体 serviceId：只对该服务生效
     *  - 返回 "*" ：对所有路由生效
     */
    @Override
    public String getRoute() {
        return "*"; // 全局熔断
    }

    /**
     * 统一 fallback 响应
     */
    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        return new ClientHttpResponse() {

            @Override
            public HttpStatus getStatusCode() {
                return HttpStatus.SERVICE_UNAVAILABLE;
            }

            @Override
            public int getRawStatusCode() {
                return HttpStatus.SERVICE_UNAVAILABLE.value();
            }

            @Override
            public String getStatusText() {
                return HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase();
            }

            @Override
            public void close() {}

            @Override
            public InputStream getBody() {
                String message = String.format(
                        "{\"code\":503,\"msg\":\"系统繁忙，请稍后重试（服务：%s）\"}",
                        // "{\"code\":503,\"message\":\"用户服务暂不可用，请稍后重试\"}"
                        route
                );
                return new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }
}

