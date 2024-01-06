package com.hoangtien2k3.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hoangtien2k3.orderservice.constrant.AppConstant;
import com.hoangtien2k3.orderservice.dto.product.ProductDto;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serial;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"cart"})
@Data
@Builder
public final class Order extends AbstractMappedEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", unique = true, nullable = false, updatable = false)
    private Integer orderId;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = AppConstant.LOCAL_DATE_TIME_FORMAT, shape = Shape.STRING)
    @DateTimeFormat(pattern = AppConstant.LOCAL_DATE_TIME_FORMAT)
    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "order_desc")
    private String orderDesc;

    @Column(name = "order_fee", columnDefinition = "decimal")
    private Double orderFee;

    @Column(name = "product_id")
    private Integer productId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cart_id")
    private Cart cart;

}

