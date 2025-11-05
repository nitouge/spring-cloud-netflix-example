package com.lsj.cloud.order.feign;

import com.lsj.cloud.common.dto.ProductDTO;
import com.lsj.cloud.common.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service",
        path = "/product",
        configuration = {FeignConfig.class},
        fallback = ProductClientFallback.class
)
public interface ProductClient {

    @GetMapping("/{id}")
    ProductDTO getProductById(@PathVariable("id") Long id);
}