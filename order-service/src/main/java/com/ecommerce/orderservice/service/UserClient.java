package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "AUTH-SERVICE")
public interface UserClient {

    @GetMapping("/api/manager/user/{userId}")
    UserDto getUserById(@PathVariable("userId") Long userId, @RequestHeader(HttpHeaders.AUTHORIZATION) String token);
}
