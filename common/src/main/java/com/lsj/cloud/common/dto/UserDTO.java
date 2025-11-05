package com.lsj.cloud.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserDTO implements Serializable {
    private Long id;

    private String username;

    private String email;

    private String realName;

    private Integer age;

    private Integer points;

    private LocalDateTime createTime;

    // 静态工厂方法创建降级用户
    public static UserDTO createFallback(Long id) {
        UserDTO user = new UserDTO();
        user.setId(id);
        user.setUsername("Fallback User");
        user.setRealName("默认用户");
        user.setPoints(0);
        return user;
    }
}