package com.lsj.cloud.product.entity;


import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(nullable = false)
    private Integer status = 1; // 1-上架，0-下架

    @Column(name = "category")
    private String category; // 商品分类

    @Column(name = "image_url")
    private String imageUrl; // 商品图片

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    // 用于测试熔断的字段
    @Transient
    private boolean simulateTimeout = false;

    @Transient
    private boolean simulateError = false;

    /**
     * 扣减库存
     */
    public boolean deductStock(Integer quantity) {
        if (this.stock < quantity) {
            return false;
        }
        this.stock -= quantity;
        return true;
    }

    /**
     * 增加库存
     */
    public void addStock(Integer quantity) {
        this.stock += quantity;
    }
}