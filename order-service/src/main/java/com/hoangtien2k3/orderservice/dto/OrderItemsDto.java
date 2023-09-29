package com.hoangtien2k3.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemsDto {

    private Long id;
    private String productName;
    private BigDecimal price;
    private Integer quantity;

}
