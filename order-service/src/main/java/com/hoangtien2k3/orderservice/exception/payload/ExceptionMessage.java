package com.hoangtien2k3.orderservice.exception.payload;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hoangtien2k3.orderservice.constrant.AppConstant;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import lombok.Builder;

@Builder
public record ExceptionMessage(
        @JsonSerialize(using = ZonedDateTimeSerializer.class)
        @JsonFormat(shape = Shape.STRING, pattern = AppConstant.ZONED_DATE_TIME_FORMAT)
        ZonedDateTime timestamp,
        @JsonInclude(value = JsonInclude.Include.NON_NULL)
        Throwable throwable,
        HttpStatus httpStatus,
        String message
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}