package com.ecommerce.orderservice.dto.inventory;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStockDto {

    private String productName;

    @JsonProperty("inStock")
    @JsonAlias("isInStock")
    private boolean inStock;

}
