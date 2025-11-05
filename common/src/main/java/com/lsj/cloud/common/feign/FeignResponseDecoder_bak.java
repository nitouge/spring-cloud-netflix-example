// package com.lsj.cloud.common.feign;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.lsj.cloud.common.core.Result;
// import feign.FeignException;
// import feign.Response;
// import feign.codec.Decoder;
// import lombok.extern.slf4j.Slf4j;
// import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
//
// import java.io.IOException;
// import java.lang.reflect.Method;
// import java.lang.reflect.ParameterizedType;
// import java.lang.reflect.Type;
//
// @Slf4j
// public class FeignResponseDecoder_bak implements Decoder {
//
//     private final ObjectMapper objectMapper;
//
//     private final Decoder delegate;
//
//     public FeignResponseDecoder_bak(Decoder decoder, ObjectMapper objectMapper) {
//         this.objectMapper = objectMapper;
//         this.delegate = decoder;
//     }
//
//     @Override
//     public Object decode(Response response, Type type) throws IOException, FeignException {
//         log.info("Feign响应状态: {}, URL: {}", response.status(), response.request().url());
//
//         Method method = response.request().requestTemplate().methodMetadata().method();
//         Class<?> returnType = method.getReturnType();
//
//         if (returnType != Result.class) {
//            Type parameterizedType = new ParameterizedType(){
//
//                 @Override
//                 public String getTypeName() {
//                     return ParameterizedType.super.getTypeName();
//                 }
//
//                 @Override
//                 public Type[] getActualTypeArguments() {
//                     return new Type[]{type};
//                 }
//
//                 @Override
//                 public Type getRawType() {
//                     return Result.class;
//                 }
//
//                 @Override
//                 public Type getOwnerType() {
//                     if (type instanceof ParameterizedTypeImpl) {
//                         return ((ParameterizedTypeImpl) type).getRawType().getEnclosingClass();
//                     }
//
//                     if (type instanceof Class) {
//                         return ((Class) type).getEnclosingClass();
//                     }
//                     return null;
//                 }
//             };
//
//             Result result = (Result) this.delegate.decode(response, parameterizedType);
//             return result.getData();
//         }
//
//         // 如果返回类型是Result，需要解包
//         // if (type.getTypeName().contains("Result")) {
//         //     Object result = delegate.decode(response, type);
//         //     log.info("原始Result对象: {}", result);
//         //
//         //     if (result instanceof Result) {
//         //         Result<?> resultObj = (Result<?>) result;
//         //         if (resultObj.isSuccess()) {
//         //             return resultObj.getData();
//         //         } else {
//         //             throw new RuntimeException("服务调用失败: " + resultObj.getMessage());
//         //         }
//         //     }
//         // }
//
//         // 直接返回
//         Object decoded = delegate.decode(response, type);
//         log.info("解码后对象: {}", decoded);
//         return decoded;
//     }
// }
