package com.hoangtien2k3.userservice.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TokenValidationRequest {
    private String accessToken;

    public TokenValidationRequest() {
    }

    public TokenValidationRequest(String accessToken) {
        this.accessToken = accessToken;
    }

}