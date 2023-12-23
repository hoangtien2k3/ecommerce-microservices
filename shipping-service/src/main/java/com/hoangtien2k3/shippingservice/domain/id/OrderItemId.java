package com.hoangtien2k3.shippingservice.domain.id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItemId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Integer productId;
    private Integer orderId;

}