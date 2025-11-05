package com.lsj.cloud.product.repository;

import com.lsj.cloud.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 根据状态查询商品
    List<Product> findByStatus(Integer status);

    // 根据名称模糊查询
    List<Product> findByNameContaining(String name);

    // 根据分类查询
    List<Product> findByCategory(String category);

    // 查询上架的商品
    List<Product> findByStatusOrderByCreateTimeDesc(Integer status);

    // 根据价格范围查询
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // 扣减库存
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :id AND p.stock >= :quantity")
    int deductStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    // 增加库存
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock + :quantity WHERE p.id = :id")
    int addStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    // 检查库存是否足够
    @Query("SELECT p.stock >= :quantity FROM Product p WHERE p.id = :id")
    Optional<Boolean> isStockSufficient(@Param("id") Long id, @Param("quantity") Integer quantity);
}