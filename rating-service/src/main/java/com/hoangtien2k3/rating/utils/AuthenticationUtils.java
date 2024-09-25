package com.hoangtien2k3.rating.utils;

import com.hoangtien2k3.rating.exception.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class AuthenticationUtils {

    private AuthenticationUtils() {
    }

    public static String extractUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException(Constants.ErrorCode.ACCESS_DENIED);
        }

        JwtAuthenticationToken contextHolder = (JwtAuthenticationToken) authentication;

        return contextHolder.getToken().getSubject();
    }
}
