package com.ecommerce.commonlib.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe formatter helpers. Formatters are immutable but compiling a new one per call
 * is wasteful — we cache by pattern string.
 */
public final class DateTimeUtils {

    public static final String DEFAULT_PATTERN = "dd-MM-yyyy_HH-mm-ss";

    private static final ConcurrentMap<String, DateTimeFormatter> FORMATTERS = new ConcurrentHashMap<>();

    private DateTimeUtils() {
    }

    public static String format(LocalDateTime dateTime) {
        return format(dateTime, DEFAULT_PATTERN);
    }

    public static String format(LocalDateTime dateTime, String pattern) {
        return FORMATTERS.computeIfAbsent(pattern, DateTimeFormatter::ofPattern).format(dateTime);
    }
}
