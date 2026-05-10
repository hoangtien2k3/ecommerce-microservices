package com.ecommerce.inventoryservice.dto.response;

public class TokenValidationResponse {
    private String message;

    public TokenValidationResponse() {
    }

    public TokenValidationResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
