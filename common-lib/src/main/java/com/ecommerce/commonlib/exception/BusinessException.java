package com.ecommerce.commonlib.exception;

import lombok.Getter;
import com.ecommerce.commonlib.utils.MessagesUtils;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public BusinessException(String message, Object... args) {
        this(HttpStatus.BAD_REQUEST, "BAD_REQUEST", MessagesUtils.getMessage(message, args));
    }

    public BusinessException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static BusinessException badRequest(String message, Object... args) {
        return new BusinessException(HttpStatus.BAD_REQUEST, "BAD_REQUEST", MessagesUtils.getMessage(message, args));
    }

    public static BusinessException unauthorized(String message, Object... args) {
        return new BusinessException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", MessagesUtils.getMessage(message, args));
    }

    public static BusinessException forbidden(String message, Object... args) {
        return new BusinessException(HttpStatus.FORBIDDEN, "FORBIDDEN", MessagesUtils.getMessage(message, args));
    }

    public static BusinessException notFound(String message, Object... args) {
        return new BusinessException(HttpStatus.NOT_FOUND, "NOT_FOUND", MessagesUtils.getMessage(message, args));
    }

    public static BusinessException conflict(String message, Object... args) {
        return new BusinessException(HttpStatus.CONFLICT, "CONFLICT", MessagesUtils.getMessage(message, args));
    }
}
