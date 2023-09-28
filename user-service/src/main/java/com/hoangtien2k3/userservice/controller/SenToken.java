package com.hoangtien2k3.userservice.controller;

import com.hoangtien2k3.userservice.dto.model.TokenManager;
import com.hoangtien2k3.userservice.entity.User;
import com.hoangtien2k3.userservice.repository.IUserRepository;
import com.hoangtien2k3.userservice.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@RequestMapping("/api/manager")
public class SenToken {

    @Autowired
    private IUserService userService;

    @Autowired
    private TokenManager tokenManager;

    @GetMapping("/token/{username}")
    public ResponseEntity<String> getTokenByUsername(@PathVariable("username") String username) {
        // Kiểm tra xác thực người dùng ở đây (ví dụ: kiểm tra session hoặc cơ sở dữ liệu).
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found."));

        // Nếu người dùng đã đăng nhập và có token, lấy token và trả về cho họ.
        String token = tokenManager.getTokenByUsername(username);

        if (token != null) {
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token not found for the username.");
        }
    }


    @GetMapping("/token")
    public ResponseEntity<String> getToken() {

        // Nếu người dùng đã đăng nhập và có token, lấy token và trả về cho họ.
        String token = tokenManager.TOKEN;

        if (token != null) {
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token not found for the username.");
        }
    }

}
