package com.lsj.cloud.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDTO implements Serializable {
    private Long id;

    private String orderNo;

    private Long userId;

    private Long productId;

    private Integer quantity;

    private BigDecimal totalAmount;

    private Integer status;

    private LocalDateTime createTime;

    // 关联信息
    private UserDTO user;

    private ProductDTO product;

    // 静态工厂方法创建降级订单
    public static OrderDTO createFallback(Long orderId, Long userId, Long productId) {
        OrderDTO order = new OrderDTO();
        order.setId(orderId);
        order.setOrderNo("FALLBACK_" + System.currentTimeMillis());
        order.setUserId(userId);
        order.setProductId(productId);
        order.setQuantity(0);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setStatus(0); // 熔断状态
        order.setUser(UserDTO.createFallback(userId));
        order.setProduct(ProductDTO.createFallback(productId));
        return order;
    }
}
