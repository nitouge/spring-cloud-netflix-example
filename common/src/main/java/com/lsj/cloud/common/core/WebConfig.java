package com.lsj.cloud.common.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());

        // 构造默认的两个处理器
        RequestResponseBodyMethodProcessor jsonProcessor = new RequestResponseBodyMethodProcessor(converters);
        ServletModelAttributeMethodProcessor formProcessor = new ServletModelAttributeMethodProcessor(false);

        // 添加自定义复合解析器
        log.info("Add CompositeArgumentResolver... resolvers size:{}", resolvers.size());
        resolvers.add(0, new CompositeArgumentResolver(jsonProcessor, formProcessor));
        log.info("Add CompositeArgumentResolver Finished");
    }
}
