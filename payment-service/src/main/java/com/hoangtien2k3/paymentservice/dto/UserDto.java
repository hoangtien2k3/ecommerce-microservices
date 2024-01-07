package com.hoangtien2k3.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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

    private Long id;
    @JsonInclude(Include.NON_NULL) private String fullname;
    @JsonInclude(Include.NON_NULL) private String username;
    @JsonInclude(Include.NON_NULL) private String email;
    @JsonInclude(Include.NON_NULL) private String gender;
    @JsonInclude(Include.NON_NULL) private String phone;
    @JsonInclude(Include.NON_NULL) private String avatar;

}