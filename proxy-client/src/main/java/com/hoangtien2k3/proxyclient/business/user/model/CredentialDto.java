package com.hoangtien2k3.proxyclient.business.user.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CredentialDto {

    private Integer credentialId;
    private String username;
    private String password;
    private RoleBasedAuthority roleBasedAuthority;
    private Boolean isEnabled;
    private Boolean isAccountNonExpired;
    private Boolean isAccountNonLocked;
    private Boolean isCredentialsNonExpired;

    @JsonProperty("user")
    @JsonInclude(value = Include.NON_NULL)
    private UserDto userDto;

    @JsonInclude(value = Include.NON_NULL)
    private Set<VerificationTokenDto> verificationTokenDtos;

}
