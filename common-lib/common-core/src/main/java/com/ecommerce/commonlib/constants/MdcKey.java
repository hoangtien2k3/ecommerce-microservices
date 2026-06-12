package com.ecommerce.commonlib.constants;

/**
 * Single source of truth for MDC keys used across logging, response envelopes,
 * and downstream propagation. Keep both the constant names and the literal values
 * in sync with the log pattern in {@code logback-spring.xml}.
 */
public final class MdcKey {

    public static final String CORRELATION_ID = "correlationId";
    public static final String REQUEST_ID = "requestId";
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    public static final String USER_ID = "userId";

    private MdcKey() {
    }
}
