# Spring Cloud 微服务演示项目

本项目演示了 Spring Cloud 核心组件的集成使用：
- Eureka：服务注册与发现
- Zuul：API 网关
- Feign：声明式服务调用
- Hystrix：熔断器
- Ribbon：客户端负载均衡

## 项目结构

```
spring-cloud-netflix-example/
├── discovery-server/         # Eureka注册中心
├── api-gateway/              # Zuul网关
├── user-service/             # 用户服务
├── product-service/          # 商品服务
├── order-service/            # 订单服务
└── common/                   # 公共模块
    ├── common-core/          # 核心公共类
    ├── common-dto/           # DTO定义
    └── common-utils/         # 工具类
```

## 启动顺序
1. discovery-server (8761)
2. user-service (8081)
3. order-service (8082)
4. api-gateway (8080)

## 测试接口

### 正常测试
```bash
# 获取用户信息
curl http://localhost:8080/api/users/1

# 创建订单
curl -X POST "http://localhost:8080/api/orders?userId=1&amount=100.0"
```
