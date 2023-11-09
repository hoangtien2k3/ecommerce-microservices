package com.hoangtien2k3qx1.favouriteservice.exception.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.hoangtien2k3qx1.favouriteservice.constant.ConfigConstant;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

// update replacement POJO old objectrecord class between
@Builder
public record ExceptionMessage(
        @JsonSerialize(using = ZonedDateTimeSerializer.class)
        @JsonFormat(pattern = ConfigConstant.ZONED_DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
        ZonedDateTime timestamp,
        @JsonInclude(value = JsonInclude.Include.NON_NULL)
        Throwable throwable,
        HttpStatus httpStatus,
        String message
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
