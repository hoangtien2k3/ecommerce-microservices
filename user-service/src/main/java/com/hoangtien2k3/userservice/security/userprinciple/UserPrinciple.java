package com.hoangtien2k3.userservice.security.userprinciple;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hoangtien2k3.userservice.model.entity.User;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@With
@Builder
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserPrinciple implements UserDetails {

    private Long id;
    private String name;
    private String username;
    private String email;
    @JsonIgnore private String password;
    private String avatar;
    private Collection<? extends GrantedAuthority> roles;

    public static UserPrinciple build(User user) {
        List<GrantedAuthority> authorityList = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.name().name()))
                .collect(Collectors.toList());

//        return new UserPrinciple(
//                user.getId(),
//                user.getName(),
//                user.getUsername(),
//                user.getEmail(),
//                user.getPassword(),
//                user.getAvatar(),
//                authorityList
//        );

        return UserPrinciple.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .avatar(user.getAvatar())
                .roles(authorityList)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
