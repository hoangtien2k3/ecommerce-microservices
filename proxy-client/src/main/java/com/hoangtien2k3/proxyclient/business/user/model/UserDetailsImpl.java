package com.hoangtien2k3.proxyclient.business.user.model;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;
    private final CredentialDto credential;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.credential.getRoleBasedAuthority().name()));
    }

    @Override
    public String getUsername() {
        return this.credential.getUsername();
    }

    @Override
    public String getPassword() {
        return this.credential.getPassword();
    }

    @Override
    public boolean isEnabled() {
        return this.credential.getIsEnabled();
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.credential.getIsAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.credential.getIsAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credential.getIsCredentialsNonExpired();
    }

}