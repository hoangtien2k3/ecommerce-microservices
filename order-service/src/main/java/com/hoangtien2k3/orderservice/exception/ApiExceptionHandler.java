package com.hoangtien2k3.orderservice.exception;

import com.hoangtien2k3.orderservice.exception.payload.ExceptionMessage;
import com.hoangtien2k3.orderservice.exception.wrapper.CartNotFoundException;
import com.hoangtien2k3.orderservice.exception.wrapper.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
    })
    public <T extends BindException> ResponseEntity<ExceptionMessage> handleValidationException(final T e) {
        return new ResponseEntity<>(
                ExceptionMessage.builder()
                        .message(Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage())
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .timestamp(ZonedDateTime.now(ZoneId.systemDefault()))
                        .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            CartNotFoundException.class,
            OrderNotFoundException.class,
            IllegalStateException.class,
    })
    public <T extends RuntimeException> ResponseEntity<ExceptionMessage> handleApiRequestException(final T e) {
        final var badRequest = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(
                ExceptionMessage.builder()
                        .message(e.getMessage())
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .timestamp(ZonedDateTime.now(ZoneId.systemDefault()))
                        .build(), HttpStatus.BAD_REQUEST);
    }

}