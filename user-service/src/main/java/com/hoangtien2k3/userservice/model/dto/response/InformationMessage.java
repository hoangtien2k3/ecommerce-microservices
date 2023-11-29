package com.hoangtien2k3.userservice.model.dto.response;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformationMessage {
    private Long id;
    private String name;
    private String username;
    private String email;
    private Collection<? extends GrantedAuthority> roles;

}
