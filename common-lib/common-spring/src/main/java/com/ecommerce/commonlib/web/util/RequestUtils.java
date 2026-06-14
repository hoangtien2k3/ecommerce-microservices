package com.ecommerce.commonlib.web.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utilities for extracting metadata from an incoming servlet HTTP request.
 * Handles reverse-proxy header chains so the real client IP is always returned.
 */
public final class RequestUtils {

    private static final String UNKNOWN = "unknown";
    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Original-Forwarded-For",
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "X-Real-IP"
    };

    private RequestUtils() {}

    /**
     * Returns the real client IP address, inspecting proxy headers before falling
     * back to {@link HttpServletRequest#getRemoteAddr()}.
     */
    public static String getClientIp(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isBlank() && !UNKNOWN.equalsIgnoreCase(ip)) {
                String resolved = ip.contains(",") ? ip.split(",")[0].trim() : ip;
                return normalize(resolved);
            }
        }
        return normalize(request.getRemoteAddr());
    }

    /** Normalizes IPv6 loopback to the familiar {@code 127.0.0.1} form. */
    private static String normalize(String ip) {
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) return "127.0.0.1";
        return ip;
    }

    public static boolean isLocalAddress(String ip) {
        return "127.0.0.1".equals(ip) || "localhost".equals(ip);
    }
}
