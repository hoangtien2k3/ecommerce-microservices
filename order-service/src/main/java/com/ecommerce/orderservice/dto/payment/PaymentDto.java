package com.ecommerce.orderservice.dto.payment;

import com.ecommerce.orderservice.dto.order.OrderDto;
import com.ecommerce.orderservice.dto.user.UserDto;
import com.ecommerce.orderservice.entity.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PaymentDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer paymentId;
    private Boolean isPayed;
    private PaymentStatus paymentStatus;

    private Integer orderId;
    private Long userId;

    @JsonProperty("order")
    @JsonInclude(Include.NON_NULL)
    private OrderDto orderDto;

    @JsonProperty("user")
    @JsonInclude(Include.NON_NULL)
    private UserDto userDto;

}
