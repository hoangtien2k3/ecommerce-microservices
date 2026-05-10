package com.ecommerce.commonlib.exception;

import com.ecommerce.commonlib.viewmodel.error.ErrorVm;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {
    private static final String ERROR_LOG_FORMAT = "Error: URI: {}, ErrorCode: {}, Message: {}";
    private static final String INVALID_REQUEST_INFORMATION_MESSAGE = "Request information is not valid";

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorVm> handleBusinessException(BusinessException ex, WebRequest request) {
        return buildErrorResponse(ex.getStatus(), ex.getMessage(), null, ex, request, ex.getStatus().value());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorVm> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                   WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + " " + error.getDefaultMessage())
            .toList();

        return buildErrorResponse(status, INVALID_REQUEST_INFORMATION_MESSAGE, errors, ex, request, 0);
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

        return buildErrorResponse(status, INVALID_REQUEST_INFORMATION_MESSAGE, errors, ex, null, status.value());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ErrorVm> handleConstraintViolation(ConstraintViolationException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<String> errors = ex.getConstraintViolations().stream()
            .map(violation -> String.format("%s %s: %s",
                violation.getRootBeanClass().getName(),
                violation.getPropertyPath(),
                violation.getMessage()))
            .toList();

        return buildErrorResponse(status, INVALID_REQUEST_INFORMATION_MESSAGE, errors, ex, null, 0);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorVm> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return handleBadRequest(ex, null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorVm> handleMissingParams(MissingServletRequestParameterException e) {
        return handleBadRequest(e, null);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorVm> handleSecurityAccessDeniedException(
        org.springframework.security.access.AccessDeniedException ex, WebRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, 403);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorVm> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, 401);
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorVm> handleRuntimeException(RuntimeException ex, WebRequest request) {
        HttpStatus status = resolveStatusFromExceptionName(ex);
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, status.value());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorVm> handleOtherException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, 500);
    }

    private String getServletPath(WebRequest webRequest) {
        ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
        return servletRequest.getRequest().getServletPath();
    }

    private ResponseEntity<ErrorVm> handleBadRequest(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, 400);
    }

    private ResponseEntity<ErrorVm> buildErrorResponse(HttpStatus status, String message, List<String> errors,
                                                       Exception ex, WebRequest request, int statusCode) {
        ErrorVm errorVm =
            new ErrorVm(status.toString(), status.getReasonPhrase(), message, errors);

        if (request != null) {
            log.error(ERROR_LOG_FORMAT, this.getServletPath(request), statusCode, message);
        }
        log.error(message, ex);
        return ResponseEntity.status(status).body(errorVm);
    }

    private HttpStatus resolveStatusFromExceptionName(RuntimeException ex) {
        String exceptionName = ex.getClass().getSimpleName().toLowerCase();

        if (exceptionName.contains("notfound")) {
            return HttpStatus.NOT_FOUND;
        }
        if (exceptionName.contains("forbidden") || exceptionName.contains("accessdenied")) {
            return HttpStatus.FORBIDDEN;
        }
        if (exceptionName.contains("unauthorized")
            || exceptionName.contains("authentication")
            || exceptionName.contains("notauthenticated")
            || exceptionName.contains("signinrequired")) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (exceptionName.contains("duplicated")
            || exceptionName.contains("alreadyexists")
            || exceptionName.contains("existed")
            || exceptionName.contains("conflict")) {
            return HttpStatus.CONFLICT;
        }
        if (exceptionName.contains("badrequest")
            || exceptionName.contains("invalid")
            || exceptionName.contains("unsupportedmedia")
            || exceptionName.contains("wrongemail")
            || exceptionName.contains("stockexisting")
            || exceptionName.contains("multipart")) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
