package com.hoangtien2k3.userservice.model.dto.request;

import lombok.*;

@Setter
@Getter
@RequiredArgsConstructor
public class UserDto {
    private Long id;
    private String fullname;
    private String username;
    private String email;
    private String gender;
    private String phone;
    private String avatar;
}
