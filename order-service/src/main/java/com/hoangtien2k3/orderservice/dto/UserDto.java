package com.hoangtien2k3.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String fullname;
    private String username;
    private String email;
    private String gender;
    private String phone;
    private String avatar;

    @JsonProperty("cart")
    @JsonInclude(Include.NON_NULL)
    private CartDto cartDto;

}