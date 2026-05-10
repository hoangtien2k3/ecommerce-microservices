package com.ecommerce.authservice.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RoleName {
    USER,
    PM,
    ADMIN
}
