package com.hoangtien2k3.userservice.model.dto.response;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponseMessage {
    private String accessToken;
    private String refreshToken;
    private InformationMessage information;
}
