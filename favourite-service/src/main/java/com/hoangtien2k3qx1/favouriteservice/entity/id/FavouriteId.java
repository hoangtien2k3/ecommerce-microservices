package com.hoangtien2k3qx1.favouriteservice.entity.id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hoangtien2k3qx1.favouriteservice.constant.ConfigConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

// Id Favourite
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FavouriteId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer productId;

    @NotNull
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = ConfigConstant.LOCAL_DATE_TIME_FORMAT, shape = Shape.STRING)
    @DateTimeFormat(pattern = ConfigConstant.LOCAL_DATE_TIME_FORMAT)
    private LocalDateTime likeDate;

}