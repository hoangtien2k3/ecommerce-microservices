package com.ecommerce.commonlib.data.audit;

import com.ecommerce.commonlib.security.AuthenticationUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * Resolves the current auditor from Spring Security: returns the JWT {@code sub} claim
 * for authenticated users, {@code "system"} for background jobs / tests with no security
 * context.
 */
public final class AuditorAwareImpl implements AuditorAware<String> {

    private static final String SYSTEM = "system";

    @Override
    public @NonNull Optional<String> getCurrentAuditor() {
        return Optional.of(AuthenticationUtils.currentUserId().orElse(SYSTEM));
    }
}
