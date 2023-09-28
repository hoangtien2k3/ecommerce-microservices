package com.hoangtien2k3.userservice.dto.model;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TokenManager {

    public String TOKEN;
    private Map<String, String> tokenStore = new HashMap<>();

    // Lưu trữ token cho một tên người dùng
    public void storeToken(String username, String token) {
        tokenStore.put(username, token);
        TOKEN = token;
    }

    // Lấy token dựa trên tên người dùng
    public String getTokenByUsername(String username) {
        return tokenStore.get(username);
    }

    // Xóa token dựa trên tên người dùng (ví dụ: khi đăng xuất)
    public void removeToken(String username) {
        tokenStore.remove(username);
    }
}