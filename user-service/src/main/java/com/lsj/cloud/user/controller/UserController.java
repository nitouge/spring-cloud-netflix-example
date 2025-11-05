package com.lsj.cloud.user.controller;


import com.lsj.cloud.common.core.Result;
import com.lsj.cloud.common.dto.UserDTO;
import com.lsj.cloud.user.entity.User;
import com.lsj.cloud.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        log.info("查询用户: {}", dto);
        return dto;
    }

    @GetMapping
    public Result<List<UserDTO>> getAllUsers() {
        log.info("获取所有用户信息");
        List<UserDTO> userDTOs = userService.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return Result.success(userDTOs);
    }

    @GetMapping("/{id}")
    public Result<UserDTO> getUserById(@PathVariable Long id) {
        log.info("根据ID查询用户: {}", id);
        // try {
        //     Thread.sleep(120_000);
        // } catch (InterruptedException e) {
        //     throw new RuntimeException(e);
        // }
        Optional<User> user = userService.findById(id);
        return user.map(u -> Result.success(convertToDTO(u)))
                .orElse(Result.fail("用户不存在"));
    }

    @PostMapping
    public Result<UserDTO> createUser(@RequestBody User user) {
        log.info("创建用户: {}", user);
        User savedUser = userService.save(user);
        return Result.success(convertToDTO(savedUser));
    }

    @GetMapping("/username/{username}")
    public Result<UserDTO> getUserByUsername(@PathVariable String username) {
        log.info("根据用户名查询用户: {}", username);
        Optional<User> user = userService.findByUsername(username);
        return user.map(u -> Result.success(convertToDTO(u)))
                .orElse(Result.fail("用户不存在"));
    }

    @GetMapping("/error-test")
    public Result<UserDTO> errorTest() {
        throw new RuntimeException("这是测试异常接口");
    }

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("User Service is UP");
    }
}
