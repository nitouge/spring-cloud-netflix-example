package com.lsj.cloud.common.enums;

import com.lsj.cloud.common.core.IResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResultEnum implements IResult {
    SUCCESS("0000", "操作成功"),
    FAIL("9999", "操作失败"),

    // 系统级
    SYSTEM_ERROR("SYS_1000", "系统繁忙，请稍后再试..."),
    DATABASE_ERROR("SYS_1001", "数据库操作失败"),
    REDIS_ERROR("SYS_1002", "缓存服务不可用"),

    // 鉴权相关
    UNAUTHORIZED("AUTH_2001", "未登录或Token已过期"),
    FORBIDDEN("AUTH_2002", "没有权限访问该资源"),

    // 参数校验
    VALIDATE_FAILED("PARAM_1001", "参数校验失败"),

    // 用户相关
    USER_NOT_FOUND("USER_2001", "用户不存在"),

    // 商品相关
    PRODUCT_NOT_FOUND("PROD_3001", "商品不存在"),
    PRODUCT_OUT_OF_STOCK("PROD_3002", "库存不足"),

    // 订单相关
    ORDER_NOT_FOUND("ORDER_4001", "订单不存在"),
    ORDER_CREATE_FAIL("ORDER_4002", "订单创建失败");

    private final String code;

    private final String message;
}


