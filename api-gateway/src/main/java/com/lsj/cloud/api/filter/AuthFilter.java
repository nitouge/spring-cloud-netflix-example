package com.lsj.cloud.api.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class AuthFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre"; // 前置过滤器
    }

    @Override
    public int filterOrder() {
        return 1; // 在CorsFilter之后执行
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        // 排除健康检查接口和预检请求
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        return !requestURI.contains("/health") && !"OPTIONS".equalsIgnoreCase(method);
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        log.info("Zuul Auth Filter: {} request to {}", request.getMethod(), request.getRequestURL());

        // 简单的Token验证（实际项目中应该更复杂）
        String token = request.getHeader("Authorization");
        if (token == null) {
            token = request.getParameter("token");
        }

        // 如果是预检请求或测试请求，跳过认证
        if (token == null && !isTestRequest(request)) {
            log.warn("Access token is empty");
            ctx.setSendZuulResponse(false); // 不进行路由
            ctx.setResponseStatusCode(401);
            ctx.setResponseBody("{\"code\":401,\"message\":\"Access token is required\"}");
            return null;
        }

        // 将用户信息传递给后续服务
        if (token != null) {
            ctx.addZuulRequestHeader("X-User-Id", extractUserIdFromToken(token));
            ctx.addZuulRequestHeader("X-User-Name", extractUserNameFromToken(token));
        }

        log.info("Auth filter passed");
        return null;
    }

    private boolean isTestRequest(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String origin = request.getHeader("Origin");

        // 测试环境或开发工具发起的请求跳过认证
        return (userAgent != null && userAgent.contains("Test")) ||
                (origin != null && origin.contains("localhost"));
    }

    private String extractUserIdFromToken(String token) {
        // 模拟从Token中提取用户ID，实际项目应该解析JWT等
        return "123";
    }

    private String extractUserNameFromToken(String token) {
        // 模拟从Token中提取用户名
        return "test_user";
    }
}
