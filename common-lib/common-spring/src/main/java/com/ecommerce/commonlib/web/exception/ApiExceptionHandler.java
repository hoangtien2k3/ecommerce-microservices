package com.ecommerce.commonlib.web.exception;

import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.commonlib.exception.ErrorCode;
import com.ecommerce.commonlib.i18n.Messages;
import com.ecommerce.commonlib.viewmodel.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Global, cross-service exception translator. Every uncaught exception in any service
 * is converted to the canonical {@link ApiResponse} envelope so clients see one shape.
 *
 * <h3>Why one handler per exception type instead of a generic mapper?</h3>
 * Each branch encodes a deliberate translation: which {@link ErrorCode}, whether to surface
 * the original message (validation) or a canned one (auth), and whether to log at WARN or ERROR.
 * A table-driven mapper hides those decisions and tends to drift.
 */
@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private static final String ERROR_LOG_FORMAT = "ApiError uri={} status={} code={} message={} cause={}";

    // ------------------------------------------------------------------
    // Business — most common, log at WARN (caller mistake, not a server bug)
    // ------------------------------------------------------------------

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex, WebRequest request) {
        return respond(ex.getStatus(), ex.getErrorCode(), ex.getMessage(), null, request, ex, false);
    }

    // ------------------------------------------------------------------
    // Validation
    // ------------------------------------------------------------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                          WebRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        return validationFailed(errors, request, ex);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleHandlerMethodValidation(HandlerMethodValidationException ex,
                                                                           WebRequest request) {
        List<String> errors = ex.getAllErrors().stream()
                .map(e -> e instanceof FieldError fe ? fe.getField() + ": " + fe.getDefaultMessage()
                                                     : e.getDefaultMessage())
                .toList();
        return validationFailed(errors, request, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex,
                                                                       WebRequest request) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
        return validationFailed(errors, request, ex);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class,
                       MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiResponse<Void>> handleMalformedRequest(Exception ex, WebRequest request) {
        return respond(HttpStatus.BAD_REQUEST,
                ErrorCode.BAD_REQUEST.getCode(),
                ex.getMessage(),
                null, request, ex, false);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableMessage(HttpMessageNotReadableException ex,
                                                                     WebRequest request) {
        // Do NOT forward ex.getMessage() — it can contain raw JSON snippets and internal parser details
        return respond(HttpStatus.BAD_REQUEST,
                ErrorCode.BAD_REQUEST.getCode(),
                Messages.get(ErrorCode.BAD_REQUEST.getMessageKey()),
                null, request, ex, false);
    }

    // ------------------------------------------------------------------
    // Routing
    // ------------------------------------------------------------------

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                      WebRequest request) {
        return respond(HttpStatus.METHOD_NOT_ALLOWED,
                ErrorCode.METHOD_NOT_ALLOWED.getCode(),
                ex.getMessage(),
                null, request, ex, false);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMediaType(HttpMediaTypeNotSupportedException ex,
                                                              WebRequest request) {
        return respond(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                ErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode(),
                ex.getMessage(),
                null, request, ex, false);
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ApiResponse<Void>> handleNotFound(Exception ex, WebRequest request) {
        return respond(HttpStatus.NOT_FOUND,
                ErrorCode.NOT_FOUND.getCode(),
                Messages.get(ErrorCode.NOT_FOUND.getMessageKey()),
                null, request, ex, false);
    }

    // ------------------------------------------------------------------
    // Upload / payload
    // ------------------------------------------------------------------

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUpload(MaxUploadSizeExceededException ex, WebRequest request) {
        return respond(HttpStatus.PAYLOAD_TOO_LARGE,
                ErrorCode.PAYLOAD_TOO_LARGE.getCode(),
                Messages.get(ErrorCode.PAYLOAD_TOO_LARGE.getMessageKey()),
                null, request, ex, false);
    }

    // ------------------------------------------------------------------
    // Data integrity
    // ------------------------------------------------------------------

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex,
                                                                 WebRequest request) {
        return respond(HttpStatus.CONFLICT,
                ErrorCode.CONFLICT.getCode(),
                ex.getMostSpecificCause().getMessage(),
                null, request, ex, true);
    }

    // ------------------------------------------------------------------
    // Security
    // ------------------------------------------------------------------

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        return respond(HttpStatus.FORBIDDEN,
                ErrorCode.FORBIDDEN.getCode(),
                Messages.get(ErrorCode.FORBIDDEN.getMessageKey()),
                null, request, ex, false);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException ex, WebRequest request) {
        return respond(HttpStatus.UNAUTHORIZED,
                ErrorCode.UNAUTHORIZED.getCode(),
                Messages.get(ErrorCode.UNAUTHORIZED.getMessageKey()),
                null, request, ex, false);
    }

    // ------------------------------------------------------------------
    // Fallback
    // ------------------------------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleOther(Exception ex, WebRequest request) {
        return respond(HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                Messages.get(ErrorCode.INTERNAL_SERVER_ERROR.getMessageKey()),
                null, request, ex, true);
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------

    private ResponseEntity<ApiResponse<Void>> validationFailed(List<String> errors,
                                                               WebRequest request,
                                                               Exception ex) {
        return respond(HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_FAILED.getCode(),
                Messages.get(ErrorCode.VALIDATION_FAILED.getMessageKey()),
                errors, request, ex, false);
    }

    private ResponseEntity<ApiResponse<Void>> respond(HttpStatus status,
                                                      String code,
                                                      String message,
                                                      List<String> errors,
                                                      WebRequest request,
                                                      Exception ex,
                                                      boolean withStack) {
        String path = resolvePath(request);
        Throwable root = getRootCause(ex);
        if (withStack) {
            log.error(ERROR_LOG_FORMAT, path, status.value(), code, message, root.getMessage(), ex);
        } else {
            log.warn(ERROR_LOG_FORMAT, path, status.value(), code, message, root.getMessage(), ex);
        }
        ApiResponse<Void> body = errors == null
                ? ApiResponse.error(code, message, path)
                : ApiResponse.error(code, message, errors, path);
        return ResponseEntity.status(status).body(body);
    }

    private static String resolvePath(WebRequest request) {
        return request instanceof ServletWebRequest swr ? swr.getRequest().getRequestURI() : null;
    }

    private static Throwable getRootCause(Throwable ex) {
        Throwable cause = ex.getCause();
        return cause != null ? getRootCause(cause) : ex;
    }
}
