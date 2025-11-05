package com.lsj.cloud.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDTO implements Serializable {
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private Integer status;

    private String category;

    private String imageUrl;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    // 静态工厂方法创建降级商品
    public static ProductDTO createFallback(Long id) {
        ProductDTO product = new ProductDTO();
        product.setId(id);
        product.setName("Fallback Product");
        product.setPrice(BigDecimal.ZERO);
        product.setStock(0);
        return product;
    }
}