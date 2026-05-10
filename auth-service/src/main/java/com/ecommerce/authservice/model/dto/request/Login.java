package com.ecommerce.authservice.model.dto.request;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class Login {

    private String username;

    private String password;
}
