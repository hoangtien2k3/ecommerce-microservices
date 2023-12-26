package com.hoangtien2k3.userservice.model.dto.request;

import lombok.*;

import java.util.Set;

@Setter
@Getter
@RequiredArgsConstructor
public class SignUpForm {
    private String fullname;
    private String username;
    private String password;
    private String email;
    private String gender;
    private String phone;
    private String avatar;
    private Set<String> roles;
}
