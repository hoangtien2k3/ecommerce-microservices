package com.hoangtien2k3.userservice.dto.request;

import lombok.*;

import java.util.Set;

@Setter
@Getter
@RequiredArgsConstructor
public class SignUpForm {
    private String name;
    private String username;
    private String email;
    private String password;
    private Set<String> roles;
}
