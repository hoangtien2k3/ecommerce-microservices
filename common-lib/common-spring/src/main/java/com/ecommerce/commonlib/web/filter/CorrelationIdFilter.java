package com.ecommerce.commonlib.web.filter;

import com.ecommerce.commonlib.constants.MdcKey;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Propagates {@code X-Correlation-Id} / {@code X-Request-Id} headers across services
 * by storing them in {@link MDC} and echoing them back to the caller.
 *
 * <p>Ordered first so every downstream log line — including ones from the Spring
 * Security filter chain — carries the ids.</p>
 *
 * <p>Wired via {@code WebAutoConfiguration}, not via {@code @Component},
 * so it can be disabled with {@code ecommerce.web.correlation.enabled=false}.</p>
 */
public final class CorrelationIdFilter extends OncePerRequestFilter implements Ordered {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        String correlationId = headerOrNew(request, CORRELATION_ID_HEADER);
        String requestId = headerOrNew(request, REQUEST_ID_HEADER);

        MDC.put(MdcKey.CORRELATION_ID, correlationId);
        MDC.put(MdcKey.REQUEST_ID, requestId);

        if (MDC.get(MdcKey.TRACE_ID) == null) {
            MDC.put(MdcKey.TRACE_ID, correlationId);
        }

        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MdcKey.CORRELATION_ID);
            MDC.remove(MdcKey.REQUEST_ID);
            MDC.remove(MdcKey.TRACE_ID);
        }
    }

    private static String headerOrNew(HttpServletRequest request, String header) {
        String value = request.getHeader(header);
        return StringUtils.hasText(value) ? value : UUID.randomUUID().toString();
    }
}
