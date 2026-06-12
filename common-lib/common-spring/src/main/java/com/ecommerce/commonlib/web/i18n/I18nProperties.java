package com.ecommerce.commonlib.web.i18n;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Locale;

/**
 * Tunables for the resource-bundle {@code MessageSource}.
 * Defaults match the {@code messages/messages_*.properties} layout shipped by common-core.
 */
@ConfigurationProperties(prefix = "ecommerce.i18n")
public record I18nProperties(
        List<String> basenames,
        Locale defaultLocale,
        Charset encoding,
        Duration cacheDuration,
        boolean fallbackToSystemLocale,
        boolean useCodeAsDefaultMessage
) {
    public I18nProperties {
        if (basenames == null || basenames.isEmpty()) {
            basenames = List.of("classpath:messages/messages");
        }
        if (defaultLocale == null) {
            defaultLocale = Locale.forLanguageTag("vi");
        }
        if (encoding == null) {
            encoding = StandardCharsets.UTF_8;
        }
        if (cacheDuration == null) {
            cacheDuration = Duration.ofHours(1);
        }
    }
}
