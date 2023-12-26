package com.hoangtien2k3.userservice.model.dto.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
public class TokenManager {
    public String TOKEN;
    public String REFRESHTOKEN;
    private Map<String, String> tokenStore = new HashMap<>();
    private Map<String, String> refreshTokenStore = new HashMap<>();

    public void storeToken(String username, String token) {
        tokenStore.put(username, token);
        TOKEN = token;
    }

    public void storeRefreshToken(String username, String refreshToken) {
        refreshTokenStore.put(username, refreshToken);
        REFRESHTOKEN = refreshToken;
    }

    public String getTokenByUsername(String username) {
        return tokenStore.get(username);
    }

    public void removeToken(String username) {
        tokenStore.remove(username);
    }

}