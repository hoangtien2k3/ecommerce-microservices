package com.hoangtien2k3.userservice.model.dto.request;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class Login {

    private String username;

    private String password;
}
