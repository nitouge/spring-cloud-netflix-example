package com.lsj.cloud.product.service;

import com.lsj.cloud.product.entity.Product;
import com.lsj.cloud.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        log.info("查询商品ID: {}", id);

        // 模拟超时 - 用于测试Hystrix熔断
        if (id == 999L) {
            try {
                Thread.sleep(5000); // 睡眠5秒，触发超时
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // 模拟异常 - 用于测试Hystrix熔断
        if (id == 888L) {
            throw new RuntimeException("模拟商品服务异常");
        }
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    public List<Product> findByNameContaining(String name) {
        return productRepository.findByNameContaining(name);
    }

    public List<Product> findByStatus(Integer status) {
        return productRepository.findByStatus(status);
    }

    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> findAvailableProducts() {
        return productRepository.findByStatusOrderByCreateTimeDesc(1);
    }

    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    /**
     * 扣减库存
     */
    @Transactional
    public boolean deductStock(Long productId, Integer quantity) {
        log.info("扣减库存，商品ID: {}, 数量: {}", productId, quantity);

        int updated = productRepository.deductStock(productId, quantity);
        if (updated > 0) {
            log.info("库存扣减成功，商品ID: {}, 数量: {}", productId, quantity);
            return true;
        } else {
            log.warn("库存扣减失败，商品ID: {}, 数量: {}", productId, quantity);
            return false;
        }
    }

    /**
     * 增加库存
     */
    @Transactional
    public boolean addStock(Long productId, Integer quantity) {
        log.info("增加库存，商品ID: {}, 数量: {}", productId, quantity);

        int updated = productRepository.addStock(productId, quantity);
        if (updated > 0) {
            log.info("库存增加成功，商品ID: {}, 数量: {}", productId, quantity);
            return true;
        } else {
            log.warn("库存增加失败，商品ID: {}, 数量: {}", productId, quantity);
            return false;
        }
    }

    /**
     * 检查库存是否足够
     */
    public boolean isStockSufficient(Long productId, Integer quantity) {
        Optional<Boolean> result = productRepository.isStockSufficient(productId, quantity);
        return result.orElse(false);
    }

    /**
     * 批量查询商品
     */
    public List<Product> findByIds(List<Long> ids) {
        return productRepository.findAllById(ids);
    }

    /**
     * 更新商品状态
     */
    @Transactional
    public boolean updateStatus(Long productId, Integer status) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setStatus(status);
            productRepository.save(product);
            log.info("更新商品状态成功，商品ID: {}, 状态: {}", productId, status);
            return true;
        }
        return false;
    }
}
