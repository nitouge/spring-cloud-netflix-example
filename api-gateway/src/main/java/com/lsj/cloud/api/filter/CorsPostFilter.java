package com.lsj.cloud.api.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class CorsPostFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "post"; // 后置过滤器
    }

    @Override
    public int filterOrder() {
        return 0; // 最先执行的后置过滤器
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

        // 添加CORS头到响应中
        String origin = request.getHeader("Origin");
        if (origin != null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, X-User-Id, X-User-Name");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "18000");
        }

        log.debug("CORS headers added to response");
        return null;
    }
}