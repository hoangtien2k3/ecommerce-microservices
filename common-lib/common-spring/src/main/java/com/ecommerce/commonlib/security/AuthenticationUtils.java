package com.ecommerce.commonlib.security;

import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.commonlib.exception.ErrorCode;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

/**
 * Convenience accessors over the current {@link SecurityContextHolder}.
 *
 * <p>Two flavors:</p>
 * <ul>
 *   <li>{@code requireXxx()} — throws {@link BusinessException} (FORBIDDEN) if not authenticated.
 *       Use from controller/service code that needs a logged-in subject to do anything.</li>
 *   <li>{@code currentXxx()} — returns {@link Optional}. Use when anonymous access is allowed.</li>
 * </ul>
 */
public final class AuthenticationUtils {

    private AuthenticationUtils() {
    }

    public static String requireUserId() {
        return currentUserId().orElseThrow(() -> BusinessException.of(ErrorCode.FORBIDDEN));
    }

    public static String requireJwt() {
        return currentJwt()
                .map(Jwt::getTokenValue)
                .orElseThrow(() -> BusinessException.of(ErrorCode.UNAUTHORIZED));
    }

    public static Optional<String> currentUserId() {
        return currentJwt().map(Jwt::getSubject);
    }

    public static Optional<Jwt> currentJwt() {
        Authentication auth = getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return Optional.ofNullable(jwtAuth.getToken());
        }
        if (auth.getPrincipal() instanceof Jwt jwt) {
            return Optional.of(jwt);
        }
        return Optional.empty();
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
