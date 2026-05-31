package com.ecommerce.commonlib.exception;

import com.ecommerce.commonlib.constants.ErrorCode;
import com.ecommerce.commonlib.utils.MessagesUtils;
import com.ecommerce.commonlib.viewmodel.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;

/**
 * Global, cross-service exception translator. Every uncaught exception in any service
 * is converted to the canonical {@link ApiResponse} envelope so clients see one shape.
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class ApiExceptionHandler {

    private static final String ERROR_LOG_FORMAT = "ApiError uri={} status={} code={} message={}";

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex, WebRequest request) {
        return build(ex.getStatus(), ex.getErrorCode(), ex.getMessage(), null, request, ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                          WebRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_FAILED.getCode(),
                MessagesUtils.getMessage(ErrorCode.VALIDATION_FAILED.getMessageKey()),
                errors, request, ex);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleHandlerMethodValidation(HandlerMethodValidationException ex,
                                                                           WebRequest request) {
        List<String> errors = ex.getAllErrors().stream()
                .map(e -> e instanceof FieldError fe ? fe.getField() + ": " + fe.getDefaultMessage()
                                                     : e.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_FAILED.getCode(),
                MessagesUtils.getMessage(ErrorCode.VALIDATION_FAILED.getMessageKey()),
                errors, request, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex,
                                                                       WebRequest request) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_FAILED.getCode(),
                MessagesUtils.getMessage(ErrorCode.VALIDATION_FAILED.getMessageKey()),
                errors, request, ex);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException ex,
                                                                WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST.getCode(), ex.getMessage(), null, request, ex);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex,
                                                                 WebRequest request) {
        return build(HttpStatus.CONFLICT, ErrorCode.CONFLICT.getCode(),
                ex.getMostSpecificCause().getMessage(), null, request, ex);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                      WebRequest request) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, ErrorCode.METHOD_NOT_ALLOWED.getCode(),
                ex.getMessage(), null, request, ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        return build(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN.getCode(),
                MessagesUtils.getMessage(ErrorCode.FORBIDDEN.getMessageKey()), null, request, ex);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException ex, WebRequest request) {
        return build(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getCode(),
                MessagesUtils.getMessage(ErrorCode.UNAUTHORIZED.getMessageKey()), null, request, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleOther(Exception ex, WebRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                MessagesUtils.getMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessageKey()),
                null, request, ex);
    }

    private ResponseEntity<ApiResponse<Void>> build(HttpStatus status, String code, String message,
                                                    List<String> errors, WebRequest request, Exception ex) {
        String path = resolvePath(request);
        log.error(ERROR_LOG_FORMAT, path, status.value(), code, message, ex);
        ApiResponse<Void> body = errors == null
                ? ApiResponse.error(code, message, path)
                : ApiResponse.error(code, message, errors, path);
        return ResponseEntity.status(status).body(body);
    }

    private String resolvePath(WebRequest request) {
        if (request instanceof ServletWebRequest swr) {
            return swr.getRequest().getRequestURI();
        }
        return null;
    }
}
