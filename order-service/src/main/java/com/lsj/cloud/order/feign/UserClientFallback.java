package com.lsj.cloud.order.feign;

import com.lsj.cloud.common.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserDTO getUserById(Long id) {
        log.warn("用户服务不可用，触发熔断降级，用户ID: {}", id);
        return UserDTO.createFallback(id);
    }

    @Override
    public UserDTO createUser(UserDTO user) {
        log.warn("用户服务不可用，创建用户失败: {}", user);
        return UserDTO.createFallback(user.getId());
    }

    @Override
    public UserDTO errorTest() {
        log.warn("用户服务异常测试接口熔断");
        return UserDTO.createFallback(0L);
    }
}