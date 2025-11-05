package com.lsj.cloud.api.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;

@Component
public class RequestStartFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre"; // 前置过滤器
    }

    @Override
    public int filterOrder() {
        return -1; // 最高优先级
    }

    @Override
    public boolean shouldFilter() {
        return true; // 始终执行
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.set("requestStartTime", System.currentTimeMillis());
        return null;
    }
}