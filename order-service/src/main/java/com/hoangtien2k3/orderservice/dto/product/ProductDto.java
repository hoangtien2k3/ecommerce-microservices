package com.hoangtien2k3.orderservice.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hoangtien2k3.orderservice.dto.order.OrderDto;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
@Builder
public class ProductDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer productId;
    private String productTitle;
    private String imageUrl;
    private String sku;
    private Double priceUnit;
    private Integer quantity;

    @JsonProperty("category")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CategoryDto categoryDto;

    @JsonProperty("order")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private OrderDto orderDto;

}
