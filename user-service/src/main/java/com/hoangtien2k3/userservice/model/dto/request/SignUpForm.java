package com.hoangtien2k3.userservice.model.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Setter
@Getter
@RequiredArgsConstructor
public class SignUpForm {

    @NotBlank
    @Size(min = 6, max = 50, message = "FullName must not be left blank")
    private String fullname;

    @NotBlank
    @Size(min = 6, max = 50, message = "FullName must not be left blank")
    private String username;

    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)[a-zA-Z\\\\d]{8,}$",
            message = "Password must contain all uppercase and lowercase letters and numbers")
    private String password;

    @Size(max = 50)
    @Pattern(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", message = "Invalid email format")
    private String email;

    @NotBlank(message = "Gender must not be left blank")
    private String gender;

    @Pattern(regexp = "^\\+84[0-9]{9,10}$|^0[0-9]{9,10}$", message = "The phone number is not in the correct format")
    private String phone;

    @Pattern(regexp = "^(http|https)://.*$", message = "Avatar URL must be a valid HTTP or HTTPS URL")
    private String avatar;

    private Set<String> roles;

}
