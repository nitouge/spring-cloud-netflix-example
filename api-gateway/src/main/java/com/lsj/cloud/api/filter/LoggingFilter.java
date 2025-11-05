package com.lsj.cloud.api.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class LoggingFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "post"; // 后置过滤器
    }

    @Override
    public int filterOrder() {
        return 1; // 优先级
    }

    @Override
    public boolean shouldFilter() {
        return true; // 始终执行
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();

        long startTime = (Long) ctx.get("requestStartTime");
        long duration = System.currentTimeMillis() - startTime;

        log.info("Zuul Post Filter: {} {} - Status: {} - Duration: {}ms",
                request.getMethod(),
                request.getRequestURL(),
                response.getStatus(),
                duration);

        return null;
    }
}