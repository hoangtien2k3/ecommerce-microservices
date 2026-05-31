package com.ecommerce.commonlib.exception;

import com.ecommerce.commonlib.constants.ErrorCode;
import com.ecommerce.commonlib.utils.MessagesUtils;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public BusinessException(ErrorCode errorCode, Object... args) {
        super(MessagesUtils.getMessage(errorCode.getMessageKey(), args));
        this.status = errorCode.getHttpStatus();
        this.errorCode = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String overrideMessageKey, Object... args) {
        super(MessagesUtils.getMessage(overrideMessageKey, args));
        this.status = errorCode.getHttpStatus();
        this.errorCode = errorCode.getCode();
    }

    public BusinessException(String message, Object... args) {
        this(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST.getCode(), MessagesUtils.getMessage(message, args));
    }

    public BusinessException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static BusinessException of(ErrorCode errorCode, Object... args) {
        return new BusinessException(errorCode, args);
    }

    public static BusinessException badRequest(String messageKey, Object... args) {
        return new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST.getCode(),
                MessagesUtils.getMessage(messageKey, args));
    }

    public static BusinessException unauthorized(String messageKey, Object... args) {
        return new BusinessException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getCode(),
                MessagesUtils.getMessage(messageKey, args));
    }

    public static BusinessException forbidden(String messageKey, Object... args) {
        return new BusinessException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN.getCode(),
                MessagesUtils.getMessage(messageKey, args));
    }

    public static BusinessException notFound(String messageKey, Object... args) {
        return new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND.getCode(),
                MessagesUtils.getMessage(messageKey, args));
    }

    public static BusinessException conflict(String messageKey, Object... args) {
        return new BusinessException(HttpStatus.CONFLICT, ErrorCode.CONFLICT.getCode(),
                MessagesUtils.getMessage(messageKey, args));
    }
}
