package com.hoangtien2k3.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class UserDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String fullname;
    private String email;
    private String gender;
    private String phone;

    @JsonProperty("cart")
    @JsonInclude(Include.NON_NULL)
    private CartDto cartDto;

}