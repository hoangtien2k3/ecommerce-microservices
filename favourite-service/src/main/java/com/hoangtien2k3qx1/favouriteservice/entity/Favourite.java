package com.hoangtien2k3qx1.favouriteservice.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.hoangtien2k3qx1.favouriteservice.constant.ConfigConstant;
import com.hoangtien2k3qx1.favouriteservice.entity.id.FavouriteId;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(FavouriteId.class)
@Getter
@Setter
@Entity
@Table(name = "favourites")
public class Favourite implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Id
    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Id
    @Column(name = "like_date", nullable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = ConfigConstant.LOCAL_DATE_TIME_FORMAT, shape = Shape.STRING)
    @DateTimeFormat(pattern = ConfigConstant.LOCAL_DATE_TIME_FORMAT)
    private LocalDateTime likeDate;

}
