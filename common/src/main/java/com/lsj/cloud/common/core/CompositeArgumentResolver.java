package com.lsj.cloud.common.core;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义参数解析器，实现不同类型参数的解析
 * 使用@RequestBody注解，Content-Type为application/x-www-form-urlencoded，使用ServletModelAttributeMethodProcessor解析器
 * 使用@RequestBody注解，Content-Type非application/x-www-form-urlencoded，使用RequestResponseBodyMethodProcessor解析器
 */
@Slf4j
public class CompositeArgumentResolver implements HandlerMethodArgumentResolver {

    private final RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor;

    private final ServletModelAttributeMethodProcessor servletModelAttributeMethodProcessor;

    private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    private static final String MULTIPART_FORM_DATA = "multipart/form-data";

    public CompositeArgumentResolver(RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor,
                                     ServletModelAttributeMethodProcessor servletModelAttributeMethodProcessor) {
        log.info(">>>>>> CompositeArgumentResolver constructor execute");
        this.requestResponseBodyMethodProcessor = requestResponseBodyMethodProcessor;
        this.servletModelAttributeMethodProcessor = servletModelAttributeMethodProcessor;
    }

    /**
     * 参数解析器适用参数条件
     *
     * @param methodParameter
     * @return 使用@RequestBody注解 返回true，未使用返回false
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        // 只处理带有@RequestBody注解的参数
        return methodParameter.hasParameterAnnotation(RequestBody.class);
    }

    /**
     * 参数解析
     *
     * @param methodParameter
     * @param modelAndViewContainer
     * @param nativeWebRequest
     * @param webDataBinderFactory
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new IllegalArgumentException("HttpServletRequest must not be null");
        }

        String contentType = request.getContentType() != null ? request.getContentType().toLowerCase() : "";
        boolean isForm = contentType.startsWith(APPLICATION_X_WWW_FORM_URLENCODED)
                || contentType.startsWith(MULTIPART_FORM_DATA);

        log.info("Using resolver [{}] for parameter [{}] due to contentType: {}",
                (isForm ? "ServletModelAttributeMethodProcessor" : "RequestResponseBodyMethodProcessor"),
                methodParameter.getParameterName(),
                contentType
        );

        // 根据Content-Type选择处理器
        if (isForm) {
            return servletModelAttributeMethodProcessor.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
        }
        return requestResponseBodyMethodProcessor.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
    }
}