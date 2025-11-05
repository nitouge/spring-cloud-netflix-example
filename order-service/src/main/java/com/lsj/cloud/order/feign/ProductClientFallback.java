package com.lsj.cloud.order.feign;

import com.lsj.cloud.common.core.Result;
import com.lsj.cloud.common.dto.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductClientFallback implements ProductClient {

    @Override
    public ProductDTO getProductById(Long id) {
        log.warn("商品服务不可用，触发熔断降级，商品ID: {}", id);
        return ProductDTO.createFallback(id);
    }
}
