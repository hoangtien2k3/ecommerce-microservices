package com.ecommerce.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ecommerce.paymentservice.entity.PaymentStatus;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Setter
@Getter
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
