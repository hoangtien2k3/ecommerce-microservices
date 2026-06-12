package com.ecommerce.commonlib.viewmodel;

import com.ecommerce.commonlib.constants.MdcKey;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.List;

/**
 * Unified response envelope returned by every endpoint in the platform.
 * Fields with {@code null} values are omitted from the JSON payload.
 *
 * @param success   outcome flag — {@code true} for 2xx, {@code false} for error responses
 * @param code      stable application code (e.g. {@code "AUTH-1001"}) or {@code "OK"} for success
 * @param message   human-readable, locale-aware summary
 * @param data      response payload (omitted on error)
 * @param errors    list of field-level / detail errors (omitted on success)
 * @param path      request URI that produced this response
 * @param traceId   correlation id propagated via MDC for distributed tracing
 * @param timestamp ISO-8601 instant the response was built
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data,
        List<String> errors,
        String path,
        String traceId,
        Instant timestamp
) {

    public static final String SUCCESS_CODE = "OK";

    public static <T> ApiResponse<T> ok(T data) {
        return success(SUCCESS_CODE, null, data);
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return success(SUCCESS_CODE, message, data);
    }

    public static ApiResponse<Void> message(String message) {
        return success(SUCCESS_CODE, message, null);
    }

    public static ApiResponse<Void> error(String code, String message, String path) {
        return new ApiResponse<>(false, code, message, null, null, path, currentTraceId(), Instant.now());
    }

    public static ApiResponse<Void> error(String code, String message, List<String> errors, String path) {
        return new ApiResponse<>(false, code, message, null, errors, path, currentTraceId(), Instant.now());
    }

    private static <T> ApiResponse<T> success(String code, String message, T data) {
        return new ApiResponse<>(true, code, message, data, null, null, currentTraceId(), Instant.now());
    }

    private static String currentTraceId() {
        return MDC.get(MdcKey.TRACE_ID);
    }
}
