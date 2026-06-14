package com.ecommerce.commonlib.web.filter;

import com.ecommerce.commonlib.constants.MdcKey;
import com.ecommerce.commonlib.web.util.RequestUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Logs every HTTP request/response for servlet-stack services (virtual thread compatible).
 *
 * <p>One log line per request, emitted after the response is written:
 * <pre>
 * path | METHOD | status | Xms | ip=... | device=... | browser=... | os=... | corrId=...[| req=...][| res=...]
 * </pre>
 *
 * <p>Log level follows response status: INFO (2xx/3xx) · WARN (4xx) · ERROR (5xx).
 * Actuator health/metrics paths are skipped automatically.
 */
@RequiredArgsConstructor
public class HttpLoggingFilter extends OncePerRequestFilter implements Ordered {

    private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final List<String> SKIP_PATHS = List.of(
            "/actuator/health/**", "/actuator/health",
            "/actuator/info", "/actuator/prometheus"
    );

    private final HttpLogProperties props;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return SKIP_PATHS.stream().anyMatch(p -> PATH_MATCHER.match(p, path));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(
                request, props.getRequest().getMaxBodyBytes());
        ContentCachingResponseWrapper res = new ContentCachingResponseWrapper(response);

        long start = System.currentTimeMillis();
        try {
            chain.doFilter(req, res);
        } finally {
            writeLine(req, res, System.currentTimeMillis() - start);
            res.copyBodyToResponse();
        }
    }

    private void writeLine(ContentCachingRequestWrapper req,
                           ContentCachingResponseWrapper res,
                           long durationMs) {
        int status     = res.getStatus();
        String ip      = RequestUtils.getClientIp(req);
        String corrId  = MDC.get(MdcKey.CORRELATION_ID);
        String ua      = req.getHeader("User-Agent");
        String referer = req.getHeader("Referer");

        StringBuilder sb = new StringBuilder()
                .append(buildPath(req))
                .append(" | ").append(req.getMethod())
                .append(" | ").append(status)
                .append(" | ").append(durationMs).append("ms")
                .append(" | ip=").append(ip)
                .append(" | ua=").append(ua != null ? ua : "-")
                .append(" | corrId=").append(corrId);

        if (referer != null && !referer.isBlank()) {
            sb.append(" | referer=").append(referer);
        }

        if (props.getRequest().isEnabled() && props.getRequest().isIncludeBody()) {
            appendBody(sb, "req", req.getContentAsByteArray(), props.getRequest().getMaxBodyBytes());
        }
        if (props.getResponse().isEnabled() && props.getResponse().isIncludeBody()) {
            appendBody(sb, "res", res.getContentAsByteArray(), props.getResponse().getMaxBodyBytes());
        }

        String line = sb.toString();
        if (status >= 500) {
            log.error(line);
        } else if (status >= 400) {
            log.warn(line);
        } else {
            log.info(line);
        }
    }

    private static String buildPath(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String query = req.getQueryString();
        String path = uri.startsWith("/") ? uri.substring(1) : uri;
        return query != null ? path + "?" + query : path;
    }

    private static void appendBody(StringBuilder sb, String label, byte[] body, int maxBytes) {
        if (body == null || body.length == 0) return;
        int len = Math.min(body.length, maxBytes);
        String content = new String(body, 0, len, StandardCharsets.UTF_8)
                .replaceAll("\\s+", " ")
                .trim();
        sb.append(" | ").append(label).append("=").append(content);
        if (body.length > maxBytes) {
            sb.append("...[truncated]");
        }
    }
}
