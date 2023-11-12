package com.hoangtien2k3.proxyclient.business.orderItem.model;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderItemDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer productId;
    private Integer orderId;
    private Integer orderedQuantity;

    @JsonProperty("product")
    @JsonInclude(Include.NON_NULL)
    private ProductDto productDto;

    @JsonProperty("order")
    @JsonInclude(Include.NON_NULL)
    private OrderDto orderDto;

}