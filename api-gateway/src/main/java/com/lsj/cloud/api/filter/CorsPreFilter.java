package com.lsj.cloud.api.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class CorsPreFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre"; // 前置过滤器
    }

    @Override
    public int filterOrder() {
        return 0; // 最高优先级，最先执行
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        // 处理预检请求
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        log.info("处理CORS预检请求: {}", request.getRequestURL());

        // 设置CORS头
        ctx.addZuulResponseHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        ctx.addZuulResponseHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ctx.addZuulResponseHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With");
        ctx.addZuulResponseHeader("Access-Control-Allow-Credentials", "true");
        ctx.addZuulResponseHeader("Access-Control-Max-Age", "18000");

        // 对于OPTIONS请求，直接返回，不进行路由
        ctx.setSendZuulResponse(false);
        ctx.setResponseStatusCode(200);

        return null;
    }
}
