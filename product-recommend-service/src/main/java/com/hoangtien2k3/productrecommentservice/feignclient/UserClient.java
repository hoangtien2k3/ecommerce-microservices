package com.hoangtien2k3.productrecommentservice.feignclient;

import com.hoangtien2k3.productrecommentservice.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8080")
public interface UserClient {

    @GetMapping(value = "/users/{id}")
    public User getUserById(@PathVariable("id") Long id);

}