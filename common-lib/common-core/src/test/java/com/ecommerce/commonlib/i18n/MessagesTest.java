package com.ecommerce.commonlib.i18n;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MessagesTest {

    @BeforeEach
    void resetLocale() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
    }

    @AfterEach
    void detach() {
        Messages.setMessageSource(null);
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void resolvesViaMessageSourceWhenAvailable() {
        MessageSource source = mock(MessageSource.class);
        when(source.getMessage("auth.user.not.found", new Object[0], Locale.ENGLISH))
                .thenReturn("User not found.");
        Messages.setMessageSource(source);

        assertThat(Messages.get("auth.user.not.found")).isEqualTo("User not found.");
    }

    @Test
    void fallsBackToBundleWhenMessageSourceMissesKey() {
        MessageSource source = mock(MessageSource.class);
        when(source.getMessage("error.bad.request", new Object[0], Locale.ENGLISH))
                .thenThrow(new NoSuchMessageException("error.bad.request"));
        Messages.setMessageSource(source);

        assertThat(Messages.get("error.bad.request")).isEqualTo("The request is invalid.");
    }

    @Test
    void returnsKeyWhenNothingResolves() {
        assertThat(Messages.get("missing.key")).isEqualTo("missing.key");
    }

    @Test
    void interpolatesArgs() {
        assertThat(Messages.get("auth.username.exists", "alice"))
                .isEqualTo("Username \"alice\" is already taken.");
    }
}
