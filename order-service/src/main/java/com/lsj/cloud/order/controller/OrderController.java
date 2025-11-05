package com.lsj.cloud.order.controller;

import com.lsj.cloud.common.core.Result;
import com.lsj.cloud.common.dto.OrderDTO;
import com.lsj.cloud.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Result<List<OrderDTO>> getAllOrders() {
        log.info("获取所有订单");
        return Result.success(orderService.findAll());
    }

    @GetMapping("/{id}")
    public Result<OrderDTO> getOrderById(@PathVariable Long id) {
        log.info("根据ID查询订单: {}", id);
        return orderService.findById(id)
                .map(Result::success)
                .orElse(Result.fail("订单不存在"));
    }

    @PostMapping
    public Result<OrderDTO> createOrder(@RequestParam Long userId,
                                        @RequestParam Long productId,
                                        @RequestParam Integer quantity) {
        log.info("创建订单，用户ID: {}, 商品ID: {}, 数量: {}", userId, productId, quantity);
        return Result.success(orderService.createOrder(userId, productId, quantity));
    }

    @GetMapping("/{id}/detail")
    public Result<OrderDTO> getOrderDetail(@PathVariable Long id) {
        log.info("获取订单详情: {}", id);
        return Result.success(orderService.getOrderDetail(id));
    }

    @GetMapping("/user/{userId}")
    public Result<List<OrderDTO>> getOrdersByUser(@PathVariable Long userId) {
        log.info("获取用户订单，用户ID: {}", userId);
        return Result.success(orderService.findByUserId(userId));
    }

    @GetMapping("/test-fallback")
    public Result<OrderDTO> testFallback(@RequestParam Long userId,
                                         @RequestParam Long productId) {
        log.info("测试熔断降级，用户ID: {}, 商品ID: {}", userId, productId);

        // 测试超时熔断 (用户ID=999)
        if (userId == 999L) {
            return Result.success(orderService.createOrder(userId, productId, 1));
        }

        // 测试异常熔断 (用户ID=888)
        if (userId == 888L) {
            return Result.success(orderService.createOrder(userId, productId, 1));
        }

        return Result.success(orderService.createOrder(userId, productId, 1));
    }

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("Order Service is UP");
    }
}
