package com.lsj.cloud.user.service;

import com.lsj.cloud.user.entity.User;
import com.lsj.cloud.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        log.info("查询用户ID: {}", id);

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
            throw new RuntimeException("模拟用户服务异常");
        }

        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
