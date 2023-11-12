package com.hoangtien2k3.proxyclient.business.favourite.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private String email;
    private String phone;

    @JsonInclude(Include.NON_NULL)
    private Set<FavouriteDto> favouriteDtos;

}