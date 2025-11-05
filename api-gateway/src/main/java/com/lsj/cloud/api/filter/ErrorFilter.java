package com.lsj.cloud.api.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ErrorFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "error"; // 错误过滤器
    }

    @Override
    public int filterOrder() {
        return 0; // 优先级
    }

    @Override
    public boolean shouldFilter() {
        return true; // 始终执行
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Throwable throwable = ctx.getThrowable();

        log.error("Zuul Error Filter: {}", throwable.getMessage(), throwable);

        ctx.setSendZuulResponse(false);
        ctx.setResponseStatusCode(500);
        ctx.setResponseBody("{\"code\":500,\"message\":\"网关内部错误: " + throwable.getMessage() + "\"}");

        return null;
    }
}