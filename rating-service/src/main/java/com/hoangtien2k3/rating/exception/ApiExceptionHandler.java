package com.hoangtien2k3.rating.exception;

import com.hoangtien2k3.rating.viewmodel.ErrorVm;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {
    private static final String ERROR_LOG_FORMAT = "Error: URI: {}, ErrorCode: {}, Message: {}";

    private String sanitizeInput(String input) {
        return input.replaceAll("[\\r\\n]", "")
                    .replaceAll("[\\t\\0\\x0B\\f]", "")
                    .replaceAll("[\\x00-\\x1F\\x7F]", "")
                    .replaceAll("[\\x80-\\x9F]", "")  // Additional control characters
                    .replaceAll("[\\u2028\\u2029]", "")  // Line separator characters
                    .replaceAll("[\\u0000-\\u001F\\u007F-\\u009F]", "");  // More control characters
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorVm> handleNotFoundException(NotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, 404, "");
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorVm> handleBadRequestException(BadRequestException ex, WebRequest request) {
        return handleBadRequest(ex, request);
    }

    @ExceptionHandler(ResourceExistedException.class)
    public ResponseEntity<ErrorVm> handleResourceExistedException(ResourceExistedException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, 409, "Resource has existed");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorVm> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + " " + error.getDefaultMessage())
            .toList();

        return buildErrorResponse(status, "Request information is not valid", errors, ex, null, 0, "");
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

        return buildErrorResponse(status, "Request information is not valid", errors, ex, null, 0, "");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorVm> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, 403, "Access Denied");
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorVm> handleOtherException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, 500, "");
    }

    private String getServletPath(WebRequest webRequest) {
        ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
        String servletPath = servletRequest.getRequest().getServletPath();
        return sanitizeInput(servletPath);
    }

    private ResponseEntity<ErrorVm> handleBadRequest(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, 400, "");
    }

    private ResponseEntity<ErrorVm> buildErrorResponse(HttpStatus status, String message, List<String> errors,
                                                       Exception ex, WebRequest request, int statusCode, String title) {
        ErrorVm errorVm =
            new ErrorVm(status.toString(), title.isEmpty() ? status.getReasonPhrase() : title, message, errors);

        if (request != null) {
            log.error(ERROR_LOG_FORMAT, sanitizeInput(this.getServletPath(request)), statusCode, sanitizeInput(message));
        }
        log.error(sanitizeInput(message), ex);
        return ResponseEntity.status(status).body(errorVm);
    }
}
