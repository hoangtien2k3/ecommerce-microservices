package com.hoangtien2k3.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hoangtien2k3.orderservice.constrant.AppConstant;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "orders")
@NoArgsConstructor(force = true)
public record Order(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "order_id", unique = true, nullable = false, updatable = false)
        Integer orderId,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = AppConstant.LOCAL_DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
        @DateTimeFormat(pattern = AppConstant.LOCAL_DATE_TIME_FORMAT)
        @Column(name = "order_date")
        LocalDateTime orderDate,
        @Column(name = "order_desc")
        String orderDesc,
        @Column(name = "order_fee", columnDefinition = "decimal")
        Double orderFee,
        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "cart_id")
        Cart cart
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}

