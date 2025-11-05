package com.lsj.cloud.order.feign;

import com.lsj.cloud.common.dto.UserDTO;
import com.lsj.cloud.common.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "user-service",
        path = "/user",
        configuration = {FeignConfig.class},
        fallback = UserClientFallback.class
)
public interface UserClient {

    @GetMapping("/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);

    @PostMapping
    UserDTO createUser(@RequestBody UserDTO user);

    @GetMapping("/error-test")
    UserDTO errorTest();
}