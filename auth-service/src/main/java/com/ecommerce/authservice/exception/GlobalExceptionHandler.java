package com.ecommerce.authservice.exception;

import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.commonlib.viewmodel.error.ErrorVm;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String ERROR_LOG_FORMAT = "Error: URI: {}, ErrorCode: {}, Message: {}";
    private static final String INVALID_REQUEST_MESSAGE = "Request information is not valid";

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorVm> handleBusinessException(BusinessException ex, WebRequest request) {
        return buildErrorResponse(ex.getStatus(), ex.getMessage(), null, ex, request, ex.getStatus().value());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorVm> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                   WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .toList();
        return buildErrorResponse(status, INVALID_REQUEST_MESSAGE, errors, ex, request, status.value());
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    protected ResponseEntity<ErrorVm> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<String> errors = ex.getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + " " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                }).toList();
        return buildErrorResponse(status, INVALID_REQUEST_MESSAGE, errors, ex, null, status.value());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorVm> handleConstraintViolation(ConstraintViolationException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<String> errors = ex.getConstraintViolations().stream()
                .map(violation -> String.format("%s %s: %s",
                        violation.getRootBeanClass().getName(),
                        violation.getPropertyPath(),
                        violation.getMessage()))
                .toList();
        return buildErrorResponse(status, INVALID_REQUEST_MESSAGE, errors, ex, null, status.value());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorVm> handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                                                WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null, ex, request, 400);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorVm> handleMissingParams(MissingServletRequestParameterException ex,
                                                          WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null, ex, request, 400);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorVm> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), null, ex, request, 403);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorVm> handleAuthentication(AuthenticationException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), null, ex, request, 401);
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorVm> handleRuntime(RuntimeException ex, WebRequest request) {
        HttpStatus status = resolveStatusFromExceptionName(ex);
        return buildErrorResponse(status, ex.getMessage(), null, ex, request, status.value());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorVm> handleOther(Exception ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null, ex, request, 500);
    }

    private String getServletPath(WebRequest webRequest) {
        if (webRequest instanceof ServletWebRequest servletRequest) {
            return servletRequest.getRequest().getServletPath();
        }
        return "";
    }

    private ResponseEntity<ErrorVm> buildErrorResponse(HttpStatus status, String message, List<String> errors,
                                                       Exception ex, WebRequest request, int statusCode) {
        ErrorVm errorVm = new ErrorVm(status.toString(), status.getReasonPhrase(), message, errors);
        if (request != null) {
            log.error(ERROR_LOG_FORMAT, this.getServletPath(request), statusCode, message);
        }
        log.error(message, ex);
        return ResponseEntity.status(status).body(errorVm);
    }

    private HttpStatus resolveStatusFromExceptionName(RuntimeException ex) {
        String name = ex.getClass().getSimpleName().toLowerCase();

        if (name.contains("notfound")) return HttpStatus.NOT_FOUND;
        if (name.contains("forbidden") || name.contains("accessdenied")) return HttpStatus.FORBIDDEN;
        if (name.contains("unauthorized") || name.contains("authentication")
                || name.contains("notauthenticated") || name.contains("signinrequired"))
            return HttpStatus.UNAUTHORIZED;
        if (name.contains("duplicated") || name.contains("alreadyexists")
                || name.contains("existed") || name.contains("conflict"))
            return HttpStatus.CONFLICT;
        if (name.contains("badrequest") || name.contains("invalid")
                || name.contains("unsupportedmedia") || name.contains("wrongemail")
                || name.contains("stockexisting") || name.contains("multipart"))
            return HttpStatus.BAD_REQUEST;

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
