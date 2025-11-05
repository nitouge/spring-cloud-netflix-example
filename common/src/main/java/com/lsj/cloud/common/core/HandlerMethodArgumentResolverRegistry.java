package com.lsj.cloud.common.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 注册参数解析器
 */
@Configuration
@Slf4j
public class HandlerMethodArgumentResolverRegistry {

    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    public HandlerMethodArgumentResolverRegistry(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
    }

    @PostConstruct
    private void addArgumentResolvers() {
        log.info("start add argument resolvers...");
        // 获取到的是不可变的集合
        List<HandlerMethodArgumentResolver> argumentResolvers = requestMappingHandlerAdapter.getArgumentResolvers();
        CompositeArgumentResolver compositeArgumentResolver = this.getCompositeArgumentResolver(argumentResolvers);
        // 新建参数解析器列表，然后将新列表添加到requestMappingHandlerAdapter中
        List<HandlerMethodArgumentResolver> newArgumentResolverList = new ArrayList<>(argumentResolvers.size() + 1);
        // 将自定义方法参数解析器放置在第一个，并保留原来的解析器
        newArgumentResolverList.add(compositeArgumentResolver);
        newArgumentResolverList.addAll(argumentResolvers);
        requestMappingHandlerAdapter.setArgumentResolvers(newArgumentResolverList);
        log.info("add argument resolvers end!!!");
    }

    /**
     * 获取自定义方法参数解析器
     * @param argumentResolversList
     * @return
     */
    private CompositeArgumentResolver getCompositeArgumentResolver(List<HandlerMethodArgumentResolver> argumentResolversList) {
        // 解析Content-Type为application/json的默认解析器
        RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor = null;
        // 解析Content-Type为application/x-www-form-urlencoded的默认解析器
        ServletModelAttributeMethodProcessor servletModelAttributeMethodProcessor = null;

        if (argumentResolversList == null) {
            throw new IllegalStateException("HandlerMethodArgumentResolver collection must not be null");
        }

        for (HandlerMethodArgumentResolver argumentResolver : argumentResolversList) {
            if (requestResponseBodyMethodProcessor != null && servletModelAttributeMethodProcessor != null) {
                break;
            }
            if (argumentResolver instanceof RequestResponseBodyMethodProcessor) {
                requestResponseBodyMethodProcessor = (RequestResponseBodyMethodProcessor) argumentResolver;
                continue;
            }
            if (argumentResolver instanceof ServletModelAttributeMethodProcessor) {
                servletModelAttributeMethodProcessor = (ServletModelAttributeMethodProcessor) argumentResolver;
            }
        }

        if (requestResponseBodyMethodProcessor == null || servletModelAttributeMethodProcessor == null) {
            throw new IllegalStateException("Method processor must not be null");
        }
        return new CompositeArgumentResolver(requestResponseBodyMethodProcessor, servletModelAttributeMethodProcessor);
    }
}