package com.hoangtien2k3.userservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpFrom {
    private String name;
    private String username;
    private String email;
    private String password;
    private Set<String> roles;
}
