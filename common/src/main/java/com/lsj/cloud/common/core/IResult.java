package com.lsj.cloud.common.core;


public interface IResult {
    /**
     * 获取状态码
     *
     * @return 状态码
     */
    String getCode();

    /**
     * 获取消息体
     *
     * @return 消息体
     */
    String getMessage();
}

