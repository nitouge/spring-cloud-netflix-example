CREATE DATABASE IF NOT EXISTS netflix_example;
USE netflix_example;

CREATE TABLE `user` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `username` varchar(50) NOT NULL COMMENT '用户名',
                        `password` varchar(100) NOT NULL COMMENT '密码',
                        `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
                        `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
                        `age` int(11) DEFAULT NULL COMMENT '年龄',
                        `points` int(11) DEFAULT '0' COMMENT '积分',
                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 测试数据
INSERT INTO `user` (username, password, email, real_name, age, points) VALUES
                                                                           ('zhangsan', '123456', 'zhangsan@example.com', '张三', 25, 1000),
                                                                           ('lisi', '123456', 'lisi@example.com', '李四', 30, 500),
                                                                           ('wangwu', '123456', 'wangwu@example.com', '王五', 28, 800);

CREATE DATABASE IF NOT EXISTS netflix_example;
USE netflix_example;

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                         `name` varchar(200) NOT NULL COMMENT '商品名称',
                                         `description` text COMMENT '商品描述',
                                         `price` decimal(10,2) NOT NULL COMMENT '价格',
                                         `stock` int(11) NOT NULL DEFAULT '0' COMMENT '库存',
                                         `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：1-上架，0-下架',
                                         `category` varchar(100) DEFAULT NULL COMMENT '商品分类',
                                         `image_url` varchar(500) DEFAULT NULL COMMENT '商品图片',
                                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                         PRIMARY KEY (`id`),
                                         KEY `idx_status` (`status`),
                                         KEY `idx_category` (`category`),
                                         KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 插入测试数据
INSERT INTO `product` (name, description, price, stock, category, image_url) VALUES
                                                                                 ('iPhone 14 Pro', '苹果最新款手机，A16芯片，4800万像素主摄', 7999.00, 100, '手机', 'https://example.com/iphone14.jpg'),
                                                                                 ('MacBook Pro 14寸', 'M2 Pro芯片，16GB内存，512GB SSD', 14999.00, 50, '电脑', 'https://example.com/macbook14.jpg'),
                                                                                 ('AirPods Pro 2', '主动降噪无线耳机，H2芯片', 1899.00, 200, '耳机', 'https://example.com/airpodspro2.jpg'),
                                                                                 ('iPad Air 5', 'M1芯片，10.9英寸 Liquid 视网膜显示屏', 4399.00, 80, '平板', 'https://example.com/ipadair5.jpg'),
                                                                                 ('Apple Watch Series 8', '全面屏设计，血氧检测，心电图功能', 2999.00, 150, '手表', 'https://example.com/applewatch8.jpg'),
                                                                                 ('小米13', '骁龙8 Gen 2，徕卡影像', 3999.00, 120, '手机', 'https://example.com/xiaomi13.jpg'),
                                                                                 ('华为MateBook 14', '2K全面屏，11代酷睿处理器', 5999.00, 60, '电脑', 'https://example.com/huaweimatebook.jpg'),
                                                                                 ('Sony WH-1000XM4', '头戴式降噪耳机，30小时续航', 2299.00, 90, '耳机', 'https://example.com/sonyxm4.jpg');

CREATE DATABASE IF NOT EXISTS netflix_example;
USE netflix_example;

CREATE TABLE `order` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                         `order_no` varchar(50) NOT NULL COMMENT '订单号',
                         `user_id` bigint(20) NOT NULL COMMENT '用户ID',
                         `product_id` bigint(20) NOT NULL COMMENT '商品ID',
                         `quantity` int(11) NOT NULL COMMENT '购买数量',
                         `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
                         `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '订单状态：1-待付款，2-已付款，3-已取消',
                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;