package com.lsj.cloud.order.repository;

import com.lsj.cloud.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNo(String orderNo);

    List<Order> findByUserId(Long userId);

    List<Order> findByProductId(Long productId);

    List<Order> findByStatus(Integer status);
}