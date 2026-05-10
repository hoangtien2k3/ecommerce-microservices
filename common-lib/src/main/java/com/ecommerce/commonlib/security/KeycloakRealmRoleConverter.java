package com.ecommerce.commonlib.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null) {
            return Collections.emptyList();
        }

        Object rolesClaim = realmAccess.get("roles");
        if (!(rolesClaim instanceof List<?> rawRoles)) {
            return Collections.emptyList();
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        rawRoles.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .forEach(role -> {
                    authorities.add(new SimpleGrantedAuthority(role));
                    String normalized = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                    authorities.add(new SimpleGrantedAuthority(normalized));
                });
        return authorities;
    }
}
