package com.hoangtien2k3.userservice.dto.request;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class SignInForm {
    private String username;
    private String password;
}
