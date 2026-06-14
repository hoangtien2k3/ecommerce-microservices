package com.ecommerce.commonlib.exception;

import com.ecommerce.commonlib.i18n.Messages;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Single business-level exception thrown by services. Carries:
 * <ul>
 *   <li>{@link ErrorCode} — machine-readable code + HTTP status</li>
 *   <li>Resolved, locale-aware message (computed eagerly at throw site)</li>
 * </ul>
 *
 * <h3>Throw sites — pick the smallest API that fits</h3>
 * <pre>{@code
 * throw BusinessException.of(ErrorCode.PRODUCT_NOT_FOUND);                     // canonical
 * throw BusinessException.of(ErrorCode.AUTH_USERNAME_EXISTS, "alice");         // with i18n args
 * throw BusinessException.notFound("auth.user.not.found.with.username", "x"); // ad-hoc + status
 * }</pre>
 *
 * <p>We <strong>do not</strong> wrap an arbitrary message + arbitrary status in a no-code
 * constructor — every error must map to an {@link ErrorCode} so dashboards stay consistent.</p>
 */
@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    private BusinessException(ErrorCode code, String message) {
        super(message);
        this.status = code.getHttpStatus();
        this.errorCode = code.getCode();
    }

    private BusinessException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.status = code.getHttpStatus();
        this.errorCode = code.getCode();
    }

    private BusinessException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    // ------------------------------------------------------------------
    // Canonical factory — preferred entry point
    // ------------------------------------------------------------------

    public static BusinessException of(ErrorCode code, Object... messageArgs) {
        return new BusinessException(code, Messages.get(code.getMessageKey(), messageArgs));
    }

    /**
     * Use when a single {@link ErrorCode} maps to multiple i18n keys depending on context
     * (e.g. {@code AUTH_USER_NOT_FOUND} surfaces as different sentences per endpoint).
     */
    public static BusinessException ofKey(ErrorCode code, String messageKey, Object... messageArgs) {
        return new BusinessException(code, Messages.get(messageKey, messageArgs));
    }

    // ------------------------------------------------------------------
    // Status shortcuts — all funnel through ErrorCode so the catalog stays canonical
    // ------------------------------------------------------------------

    public static BusinessException badRequest(String messageKey, Object... args) {
        return new BusinessException(ErrorCode.BAD_REQUEST, Messages.get(messageKey, args));
    }

    public static BusinessException badRequest(String messageKey, Throwable cause, Object... args) {
        return new BusinessException(ErrorCode.BAD_REQUEST, Messages.get(messageKey, args), cause);
    }

    public static BusinessException unauthorized(String messageKey, Object... args) {
        return new BusinessException(ErrorCode.UNAUTHORIZED, Messages.get(messageKey, args));
    }

    public static BusinessException unauthorized(String messageKey, Throwable cause, Object... args) {
        return new BusinessException(ErrorCode.UNAUTHORIZED, Messages.get(messageKey, args), cause);
    }

    public static BusinessException forbidden(String messageKey, Object... args) {
        return new BusinessException(ErrorCode.FORBIDDEN, Messages.get(messageKey, args));
    }

    public static BusinessException forbidden(String messageKey, Throwable cause, Object... args) {
        return new BusinessException(ErrorCode.FORBIDDEN, Messages.get(messageKey, args), cause);
    }

    public static BusinessException notFound(String messageKey, Object... args) {
        return new BusinessException(ErrorCode.NOT_FOUND, Messages.get(messageKey, args));
    }

    public static BusinessException notFound(String messageKey, Throwable cause, Object... args) {
        return new BusinessException(ErrorCode.NOT_FOUND, Messages.get(messageKey, args), cause);
    }

    public static BusinessException conflict(String messageKey, Object... args) {
        return new BusinessException(ErrorCode.CONFLICT, Messages.get(messageKey, args));
    }

    public static BusinessException conflict(String messageKey, Throwable cause, Object... args) {
        return new BusinessException(ErrorCode.CONFLICT, Messages.get(messageKey, args), cause);
    }

    public static BusinessException internalServerError(String messageKey, Object... args) {
        return new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, Messages.get(messageKey, args));
    }

    public static BusinessException internalServerError(String messageKey, Throwable cause, Object... args) {
        return new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, Messages.get(messageKey, args), cause);
    }
}
