package com.hoangtien2k3.proxyclient.business.user.model;

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
public class AddressDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer addressId;

    private String fullAddress;

    private String postalCode;

    private String city;

    @JsonProperty("user")
    @JsonInclude(value = Include.NON_NULL)
    private UserDto userDto;

}