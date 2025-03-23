package com.hoangtien2k3.commonlib.exception;

import com.hoangtien2k3.commonlib.viewmodel.error.ErrorVm;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorVm> handleNotFoundException(NotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, 404);
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

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorVm> handleOtherException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, 500);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorVm> handleBadRequestException(BadRequestException ex, WebRequest request) {
        return handleBadRequest(ex, request);
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

    @ExceptionHandler(DuplicatedException.class)
    protected ResponseEntity<ErrorVm> handleDuplicated(DuplicatedException ex) {
        return handleBadRequest(ex, null);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    protected ResponseEntity<ErrorVm> handleInternalServerErrorException(InternalServerErrorException e) {
        log.error("Internal server error exception: ", e);
        ErrorVm errorVm = new ErrorVm(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage());
        return ResponseEntity.internalServerError().body(errorVm);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorVm> handleMissingParams(MissingServletRequestParameterException e) {
        return handleBadRequest(e, null);
    }

    @ExceptionHandler(ResourceExistedException.class)
    public ResponseEntity<ErrorVm> handleResourceExistedException(ResourceExistedException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, 409);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorVm> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, 403);
    }

    @ExceptionHandler(WrongEmailFormatException.class)
    public ResponseEntity<ErrorVm> handleWrongEmailFormatException(WrongEmailFormatException ex, WebRequest request) {
        return handleBadRequest(ex, request);
    }

    @ExceptionHandler(CreateGuestUserException.class)
    public ResponseEntity<ErrorVm> handleCreateGuestUserException(CreateGuestUserException ex, WebRequest request) {
        return handleBadRequest(ex, request);
    }

    @ExceptionHandler(StockExistingException.class)
    public ResponseEntity<ErrorVm> handleStockExistingException(StockExistingException ex, WebRequest request) {
        return handleBadRequest(ex, request);
    }

    @ExceptionHandler({SignInRequiredException.class})
    public ResponseEntity<ErrorVm> handleSignInRequired(SignInRequiredException ex) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, null, 403);
    }

    @ExceptionHandler({Forbidden.class})
    public ResponseEntity<ErrorVm> handleForbidden(NotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, 403);
    }

    private String getServletPath(WebRequest webRequest) {
        ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
        String servletPath = servletRequest.getRequest().getServletPath();
        return sanitizeInput(servletPath);
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

    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // Replace new-line characters and other control characters
        return input.replaceAll("[\\r\\n]", "_");
    }
}