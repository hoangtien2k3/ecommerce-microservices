package com.hoangtien2k3.proxyclient.business.auth.model.response;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AuthenticationResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String jwtToken;

}