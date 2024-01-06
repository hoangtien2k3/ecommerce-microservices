package com.hoangtien2k3.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hoangtien2k3.paymentservice.entity.PaymentStatus;
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

    @JsonProperty("order")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private OrderDto orderDto;

}
