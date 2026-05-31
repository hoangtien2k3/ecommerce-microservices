package com.ecommerce.commonlib.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Propagates a {@code X-Correlation-Id} and {@code X-Request-Id} across services
 * by storing both in {@link MDC} and echoing them back to the caller.
 * Picked up by every Spring Boot service that scans {@code com.ecommerce.commonlib}.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";
    public static final String REQUEST_ID_MDC_KEY = "requestId";
    public static final String TRACE_ID_MDC_KEY = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String correlationId = headerOrNew(request, CORRELATION_ID_HEADER);
        String requestId = headerOrNew(request, REQUEST_ID_HEADER);

        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
        MDC.put(REQUEST_ID_MDC_KEY, requestId);
        MDC.put(TRACE_ID_MDC_KEY, correlationId);

        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(CORRELATION_ID_MDC_KEY);
            MDC.remove(REQUEST_ID_MDC_KEY);
            MDC.remove(TRACE_ID_MDC_KEY);
        }
    }

    private String headerOrNew(HttpServletRequest request, String header) {
        String value = request.getHeader(header);
        return StringUtils.hasText(value) ? value : UUID.randomUUID().toString();
    }
}
