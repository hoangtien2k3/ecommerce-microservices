package com.hoangtien2k3.userservice.model.dto.request;

import lombok.*;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Setter
@Getter
@RequiredArgsConstructor
public class SignUpForm {

    private String fullname;
    private String username;

    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @Size(max = 50)
    @Pattern(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", message = "Invalid email format")
    private String email;

    private String gender;
    private String phone;
    private String avatar;
    private Set<String> roles;

}
