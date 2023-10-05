package com.hoangtien2k3.userservice.dto.model;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TokenManager {

    public String TOKEN;
    public String REFRESHTOKEN;
    private Map<String, String> tokenStore = new HashMap<>();
    private Map<String, String> refreshTokenStore = new HashMap<>();

    // Lưu trữ token cho một tên người dùng
    public void storeToken(String username, String token) {
        tokenStore.put(username, token);
        TOKEN = token;
    }

    public void storeRefreshToken(String username, String refreshToken) {
        refreshTokenStore.put(username, refreshToken);
        REFRESHTOKEN = refreshToken;
    }


    // Lấy token dựa trên tên người dùng
    public String getTokenByUsername(String username) {
        return tokenStore.get(username);
    }

    // Xóa token dựa trên tên người dùng (ví dụ: khi đăng xuất)
    public void removeToken(String username) {
        tokenStore.remove(username);
    }

    public String getTOKEN() {
        return TOKEN;
    }

    public void setTOKEN(String TOKEN) {
        this.TOKEN = TOKEN;
    }

    public String getREFRESHTOKEN() {
        return REFRESHTOKEN;
    }

    public void setREFRESHTOKEN(String REFRESHTOKEN) {
        this.REFRESHTOKEN = REFRESHTOKEN;
    }

    public Map<String, String> getTokenStore() {
        return tokenStore;
    }

    public void setTokenStore(Map<String, String> tokenStore) {
        this.tokenStore = tokenStore;
    }

    public Map<String, String> getRefreshTokenStore() {
        return refreshTokenStore;
    }

    public void setRefreshTokenStore(Map<String, String> refreshTokenStore) {
        this.refreshTokenStore = refreshTokenStore;
    }
}