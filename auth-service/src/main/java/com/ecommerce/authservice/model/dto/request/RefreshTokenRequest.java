package com.ecommerce.authservice.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {
    @NotBlank(message = "refreshToken must not be blank")
    private String refreshToken;
}
