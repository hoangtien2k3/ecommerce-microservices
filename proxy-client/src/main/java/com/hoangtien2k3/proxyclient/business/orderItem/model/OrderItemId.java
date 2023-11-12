package com.hoangtien2k3.proxyclient.business.orderItem.model;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItemId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer productId;
    private Integer orderId;

}