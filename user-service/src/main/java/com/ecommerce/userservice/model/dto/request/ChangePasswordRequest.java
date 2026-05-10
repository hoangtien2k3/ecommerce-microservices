package com.ecommerce.userservice.model.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ChangePasswordRequest {
    String oldPassword;
    String newPassword;
    String confirmPassword;
}
