package com.hoangtien2k3.proxyclient.business.order.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

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
public class CartDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer cartId;
    private Integer userId;

    @JsonInclude(Include.NON_NULL)
    private Set<OrderDto> orderDtos;

    @JsonProperty("user")
    @JsonInclude(Include.NON_NULL)
    private UserDto userDto;

}