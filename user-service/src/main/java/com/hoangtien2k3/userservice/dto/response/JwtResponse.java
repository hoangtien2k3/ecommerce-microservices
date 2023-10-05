package com.hoangtien2k3.userservice.dto.response;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
@RequiredArgsConstructor
public class JwtResponse {
    private Long id;
    private String accessToken;
    private String refreshToken;
    private String type = "Bearer";
    private String name;
    private Collection<? extends GrantedAuthority> roles;

    public JwtResponse(String accessToken, String refreshToken, Long id, String name, Collection<? extends GrantedAuthority> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.name = name;
        this.roles = roles;
    }
}
