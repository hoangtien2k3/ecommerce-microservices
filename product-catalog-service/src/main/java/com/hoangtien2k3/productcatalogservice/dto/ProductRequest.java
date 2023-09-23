package com.hoangtien2k3.productcatalogservice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@RequiredArgsConstructor
public class ProductRequest {
    private String productName;
    private BigDecimal price;
    private String description;
    private String category;
    private int availability;
}
