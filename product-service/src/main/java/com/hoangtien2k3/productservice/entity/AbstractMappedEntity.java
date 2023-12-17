package com.hoangtien2k3.productservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public abstract class AbstractMappedEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @CreatedDate
    @JsonFormat(shape = Shape.STRING)
    @Column(name = "created_at")
    private Instant createAt;

    @LastModifiedDate
    @JsonFormat(shape = Shape.STRING)
    @Column(name = "updated_at")
    private Instant updateAt;

}
