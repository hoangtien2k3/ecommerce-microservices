package com.ecommerce.paymentservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public final class JwtTokenFilter {

    private JwtTokenFilter() {
    }

    public static String getTokenFromRequest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            return "";
        }
        return "Bearer " + jwtAuthenticationToken.getToken().getTokenValue();
    }
}
