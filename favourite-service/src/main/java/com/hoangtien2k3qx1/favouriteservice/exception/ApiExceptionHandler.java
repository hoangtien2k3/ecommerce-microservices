package com.hoangtien2k3qx1.favouriteservice.exception;

import com.hoangtien2k3qx1.favouriteservice.exception.payload.ExceptionMessage;
import com.hoangtien2k3qx1.favouriteservice.exception.wrapper.FavouriteNotFoundException;
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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
    })
    public <T extends BindException> ResponseEntity<ExceptionMessage> handleValidationException(final T exception) {
        return new ResponseEntity<>(
                ExceptionMessage.builder()
                        .timestamp(ZonedDateTime.now(ZoneId.systemDefault())) // ZonedDateTime Id default
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message(Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage())
                        .throwable(exception)
                        .build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {FavouriteNotFoundException.class})
    public <T extends RuntimeException> ResponseEntity<ExceptionMessage> handleApiRequestException(final T exception) {
        return new ResponseEntity<>(
                ExceptionMessage.builder()
                        .message(String.valueOf(Optional.ofNullable(exception.getMessage())))
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .timestamp(ZonedDateTime.now(ZoneId.systemDefault()))
                        .throwable(exception)
                        .build(),
                HttpStatus.BAD_REQUEST);
    }

}
