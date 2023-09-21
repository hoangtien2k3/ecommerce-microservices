package com.hoangtien2k3.orderserivce.feignclient;

import com.hoangtien2k3.orderserivce.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "User", url = "http://localhost:8080/")
public interface UserClient {
    @GetMapping(value = "/users/{id}")
    User getUserById(@PathVariable("id") Long id);
}
