package com.lsj.cloud.product.controller;

import com.lsj.cloud.common.core.Result;
import com.lsj.cloud.common.dto.ProductDTO;
import com.lsj.cloud.product.entity.Product;
import com.lsj.cloud.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(product, dto);
        log.info("查询商品: {}", dto);
        return dto;
    }

    /**
     * 获取所有商品
     */
    @GetMapping
    public Result<List<ProductDTO>> getAllProducts() {
        log.info("获取所有商品");
        List<ProductDTO> productDTOs = productService.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return Result.success(productDTOs);
    }

    /**
     * 根据ID获取商品
     */
    @GetMapping("/{id}")
    public Result<ProductDTO> getProductById(@PathVariable Long id) {
        log.info("根据ID查询商品: {}", id);
        Optional<Product> product = productService.findById(id);
        return product.map(p -> Result.success(convertToDTO(p)))
                .orElse(Result.fail("商品不存在"));
    }

    /**
     * 创建商品
     */
    @PostMapping
    public Result<ProductDTO> createProduct(@RequestBody Product product) {
        log.info("创建商品: {}", product.getName());
        Product savedProduct = productService.save(product);
        return Result.success(convertToDTO(savedProduct));
    }

    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public Result<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        log.info("更新商品ID: {}", id);
        if (!productService.existsById(id)) {
            return Result.fail("商品不存在");
        }
        product.setId(id);
        Product updatedProduct = productService.save(product);
        return Result.success(convertToDTO(updatedProduct));
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteProduct(@PathVariable Long id) {
        log.info("删除商品ID: {}", id);
        if (!productService.existsById(id)) {
            return Result.fail("商品不存在");
        }
        productService.deleteById(id);
        return Result.success("删除成功");
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public Result<List<ProductDTO>> searchProducts(@RequestParam String name) {
        log.info("搜索商品: {}", name);
        List<ProductDTO> productDTOs = productService.findByNameContaining(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return Result.success(productDTOs);
    }

    /**
     * 根据分类获取商品
     */
    @GetMapping("/category/{category}")
    public Result<List<ProductDTO>> getProductsByCategory(@PathVariable String category) {
        log.info("根据分类查询商品: {}", category);
        List<ProductDTO> productDTOs = productService.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return Result.success(productDTOs);
    }

    /**
     * 获取上架商品
     */
    @GetMapping("/available")
    public Result<List<ProductDTO>> getAvailableProducts() {
        log.info("获取上架商品");
        List<ProductDTO> productDTOs = productService.findAvailableProducts().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return Result.success(productDTOs);
    }

    /**
     * 根据价格范围查询商品
     */
    @GetMapping("/price-range")
    public Result<List<ProductDTO>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        log.info("根据价格范围查询商品: {} - {}", minPrice, maxPrice);
        List<ProductDTO> productDTOs = productService.findByPriceRange(minPrice, maxPrice).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return Result.success(productDTOs);
    }

    /**
     * 扣减库存
     */
    @PostMapping("/{id}/deduct-stock")
    public Result<String> deductStock(@PathVariable Long id, @RequestParam Integer quantity) {
        log.info("扣减库存，商品ID: {}, 数量: {}", id, quantity);
        boolean success = productService.deductStock(id, quantity);
        if (success) {
            return Result.success("库存扣减成功");
        } else {
            return Result.fail("库存不足");
        }
    }

    /**
     * 增加库存
     */
    @PostMapping("/{id}/add-stock")
    public Result<String> addStock(@PathVariable Long id, @RequestParam Integer quantity) {
        log.info("增加库存，商品ID: {}, 数量: {}", id, quantity);
        boolean success = productService.addStock(id, quantity);
        if (success) {
            return Result.success("库存增加成功");
        } else {
            return Result.fail("库存增加失败");
        }
    }

    /**
     * 检查库存
     */
    @GetMapping("/{id}/check-stock")
    public Result<Boolean> checkStock(@PathVariable Long id, @RequestParam Integer quantity) {
        log.info("检查库存，商品ID: {}, 数量: {}", id, quantity);
        boolean sufficient = productService.isStockSufficient(id, quantity);
        return Result.success(sufficient);
    }

    /**
     * 更新商品状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("更新商品状态，商品ID: {}, 状态: {}", id, status);
        boolean success = productService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.fail("商品不存在");
        }
    }

    /**
     * 批量获取商品信息
     */
    @PostMapping("/batch")
    public Result<List<ProductDTO>> getProductsBatch(@RequestBody List<Long> ids) {
        log.info("批量获取商品信息: {}", ids);
        List<ProductDTO> productDTOs = productService.findByIds(ids).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return Result.success(productDTOs);
    }

    /**
     * 测试异常接口
     */
    @GetMapping("/error-test")
    public Result<ProductDTO> errorTest() {
        throw new RuntimeException("这是商品服务测试异常接口");
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("Product Service is UP");
    }

    /**
     * 模拟超时接口 - 用于测试Hystrix熔断
     */
    @GetMapping("/timeout-test")
    public Result<String> timeoutTest() {
        log.info("模拟超时接口");
        try {
            Thread.sleep(5000); // 睡眠5秒，触发超时
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Result.success("超时测试完成");
    }
}