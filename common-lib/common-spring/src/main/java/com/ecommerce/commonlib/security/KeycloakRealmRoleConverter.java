package com.ecommerce.commonlib.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Maps Keycloak's {@code realm_access.roles} claim into Spring's {@link GrantedAuthority}s.
 *
 * <p>For each role we emit <strong>two</strong> authorities:</p>
 * <ul>
 *   <li>The raw role name (so {@code .hasAuthority("admin")} works for scope-style checks)</li>
 *   <li>The {@code ROLE_} prefixed form (so {@code .hasRole("admin")} works too)</li>
 * </ul>
 * Emitting both keeps the converter compatible with both styles services already use.
 */
public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String REALM_ACCESS = "realm_access";
    private static final String ROLES = "roles";
    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap(REALM_ACCESS);
        if (realmAccess == null) {
            return List.of();
        }
        Object rolesClaim = realmAccess.get(ROLES);
        if (!(rolesClaim instanceof List<?> rawRoles) || rawRoles.isEmpty()) {
            return List.of();
        }

        List<GrantedAuthority> authorities = new ArrayList<>(rawRoles.size() * 2);
        for (Object raw : rawRoles) {
            if (raw == null) {
                continue;
            }
            String role = Objects.toString(raw);
            authorities.add(new SimpleGrantedAuthority(role));
            authorities.add(new SimpleGrantedAuthority(role.startsWith(ROLE_PREFIX) ? role : ROLE_PREFIX + role));
        }
        return authorities;
    }
}
