package com.ecommerce.commonlib.logging;

/**
 * Immutable record capturing all fields written to the performance log line.
 *
 * <p>Kept as a plain record so it can be serialised to structured JSON later
 * (e.g. via a Logback encoder) without changing the aspect code.
 */
public record LogField(
        String corrId,
        String service,
        String action,
        String title,
        long durationMs,
        String result,
        String inputs,
        String output
) {
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR   = "ERROR";
}
