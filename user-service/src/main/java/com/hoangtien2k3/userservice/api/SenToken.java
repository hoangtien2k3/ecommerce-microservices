package com.hoangtien2k3.userservice.api;

import com.hoangtien2k3.userservice.model.dto.model.TokenManager;
import com.hoangtien2k3.userservice.model.entity.User;
import com.hoangtien2k3.userservice.repository.IUserRepository;
import com.hoangtien2k3.userservice.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequestMapping("/api/manager")
public class SenToken {

    private final IUserService userService;
    private final IUserRepository userRepository;
    private final TokenManager tokenManager;

    @Autowired
    public SenToken(IUserService userService, IUserRepository userRepository, TokenManager tokenManager) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.tokenManager = tokenManager;
    }

    @PreAuthorize(value = "hasRole('USER')")
    @GetMapping("/token/{username}")
    public ResponseEntity<String> getTokenByUsername(@PathVariable("username") String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found."));

        String token = null;
        if (user != null) {
            token = tokenManager.getTokenByUsername(username);
        }

        return (token != null)
                ? ResponseEntity.ok(token)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token not found for the username.");
    }

    @GetMapping("/token")
    public ResponseEntity<String> getToken() {
        String token = tokenManager.TOKEN;
        return (token != null)
                ? ResponseEntity.ok(token)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token not found for the username.");
    }

}
