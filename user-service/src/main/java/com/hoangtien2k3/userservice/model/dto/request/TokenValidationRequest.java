package com.hoangtien2k3.userservice.model.dto.request;

import lombok.Getter;

@Getter
public class TokenValidationRequest {
    private String accessToken;

    public TokenValidationRequest() {
    }

    public TokenValidationRequest(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}