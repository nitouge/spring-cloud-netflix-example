package com.lsj.cloud.order.service;

import com.lsj.cloud.common.dto.OrderDTO;
import com.lsj.cloud.common.dto.ProductDTO;
import com.lsj.cloud.common.dto.UserDTO;
import com.lsj.cloud.order.entity.Order;
import com.lsj.cloud.order.feign.ProductClient;
import com.lsj.cloud.order.feign.UserClient;
import com.lsj.cloud.order.repository.OrderRepository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ProductClient productClient;

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(order, dto);
        return dto;
    }

    private OrderDTO convertToDetailDTO(Order order, UserDTO user, ProductDTO product) {
        OrderDTO dto = convertToDTO(order);
        dto.setUser(user);
        dto.setProduct(product);
        return dto;
    }

    public List<OrderDTO> findAll() {
        return orderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<OrderDTO> findById(Long id) {
        return orderRepository.findById(id)
                .map(this::convertToDTO);
    }

    @HystrixCommand(
            fallbackMethod = "createOrderFallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000")
            }
    )
    public OrderDTO createOrder(Long userId, Long productId, Integer quantity) {
        log.info("开始创建订单，用户ID: {}, 商品ID: {}, 数量: {}", userId, productId, quantity);

        // 1. 调用用户服务获取用户信息
        UserDTO user = userClient.getUserById(userId);
        log.info("获取到用户信息: {}", user.getRealName());

        // 2. 调用商品服务获取商品信息
        ProductDTO product = productClient.getProductById(productId);
        log.info("获取到商品信息: {}, 价格: {}", product.getName(), product.getPrice());

        // 3. 校验库存
        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        // 4. 计算总金额
        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(quantity));

        // 5. 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalAmount(totalAmount);
        order.setStatus(1); // 待付款
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        log.info("订单创建成功: {}", savedOrder.getOrderNo());

        return convertToDetailDTO(savedOrder, user, product);
    }

    public OrderDTO createOrderFallback(Long userId, Long productId, Integer quantity, Throwable throwable) {
        log.error("创建订单服务熔断，用户ID: {}, 商品ID: {}, 错误: {}", userId, productId, throwable.getMessage());
        return OrderDTO.createFallback(null, userId, productId);
    }

    @HystrixCommand(fallbackMethod = "getOrderDetailFallback")
    public OrderDTO getOrderDetail(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();

            // 获取用户信息
            UserDTO user = userClient.getUserById(order.getUserId());

            // 获取商品信息
            ProductDTO product = productClient.getProductById(order.getProductId());

            return convertToDetailDTO(order, user, product);
        }
        throw new RuntimeException("订单不存在");
    }

    public OrderDTO getOrderDetailFallback(Long orderId, Throwable throwable) {
        log.error("获取订单详情熔断，订单ID: {}, 错误: {}", orderId, throwable.getMessage());
        return OrderDTO.createFallback(orderId, null, null);
    }

    public List<OrderDTO> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private String generateOrderNo() {
        return "ORDER_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}