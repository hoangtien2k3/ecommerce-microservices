package com.ecommerce.commonlib.utils;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.context.i18n.LocaleContextHolder;

public class MessagesUtils {
    private static final String BUNDLE_BASENAME = "messages.messages";
    private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("vi");

    private MessagesUtils() {
    }

    public static String getMessage(String errorCode, Object... var2) {
        ResourceBundle messageBundle = ResourceBundle.getBundle(BUNDLE_BASENAME, resolveLocale());
        String message;
        try {
            message = messageBundle.getString(errorCode);
        } catch (MissingResourceException ex) {
            message = errorCode;
        }
        FormattingTuple formattingTuple = MessageFormatter.arrayFormat(message, var2);
        return formattingTuple.getMessage();
    }

    private static Locale resolveLocale() {
        Locale requestLocale = LocaleContextHolder.getLocale();
        if ("en".equalsIgnoreCase(requestLocale.getLanguage())) {
            return Locale.ENGLISH;
        }
        return DEFAULT_LOCALE;
    }
}
