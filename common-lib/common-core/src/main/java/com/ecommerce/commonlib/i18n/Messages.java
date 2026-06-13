package com.ecommerce.commonlib.i18n;

import lombok.Setter;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Static i18n facade used from non-Spring contexts (exception constructors, utility code).
 *
 * <p>Resolution order:</p>
 * <ol>
 *   <li>Spring-managed {@link MessageSource} if it has been installed via {@link #setMessageSource}.
 *       This is the path that runs at request time — it uses the locale resolver,
 *       respects cached bundles, and supports nested fallbacks.</li>
 *   <li>A {@link ResourceBundle} loaded from {@code messages/messages_*.properties} on the
 *       classpath — used during early bootstrap, in unit tests, and from non-web modules.</li>
 *   <li>The raw {@code messageKey} when nothing else matches, so error responses always
 *       contain <em>something</em> instead of crashing the handler.</li>
 * </ol>
 *
 * <p>Format placeholders use {@link MessageFormat} {@code {0}} indexed syntax — the same
 * syntax Spring's {@code MessageSource} understands so templates are interpreted identically
 * on both lookup paths.</p>
 */
public final class Messages {

    private static final String BUNDLE_BASENAME = "messages.messages";

    @Setter
    private static volatile MessageSource messageSource;

    private Messages() {
    }

    public static String get(String messageKey, Object... args) {
        if (messageKey == null) {
            return null;
        }
        Locale locale = LocaleContextHolder.getLocale();

        MessageSource source = messageSource;
        if (source != null) {
            try {
                return source.getMessage(messageKey, args, locale);
            } catch (NoSuchMessageException ignored) {
                // fall through to the bundle path
            }
        }

        return fromBundle(messageKey, locale, args);
    }

    private static String fromBundle(String messageKey, Locale locale, Object[] args) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASENAME, locale);
            String template = bundle.getString(messageKey);
            return args == null || args.length == 0
                    ? template
                    : new MessageFormat(template, locale).format(args);
        } catch (MissingResourceException ex) {
            return messageKey;
        }
    }
}
