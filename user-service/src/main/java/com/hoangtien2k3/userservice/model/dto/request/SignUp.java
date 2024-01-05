package com.hoangtien2k3.userservice.model.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Setter
@Getter
@RequiredArgsConstructor
public class SignUp {
    @NotBlank(message = "The fullName must not be left blank")
    @Size(min = 6, max = 50, message = "The fullName must be 6 characters or more")
    private String fullname;

    @NotBlank(message = "The username must not be left blank")
    @Size(min = 6, max = 50, message = "The username must be 6 characters or more")
    private String username;

    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$",
            message = "Password must contain all uppercase and lowercase letters and numbers")
    private String password;

    @Size(max = 50)
    @Pattern(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", message = "Invalid email format")
    private String email;

    @NotBlank(message = "Gender must not be left blank")
    private String gender;

    @Size(min = 10, max = 11, message = "Phone number must be between 10 and 11 digits")
    @Pattern(regexp = "^\\+84[0-9]{9,10}$|^0[0-9]{9,10}$", message = "The phone number is not in the correct format")
    private String phone;

    @Pattern(regexp = "^(http|https)://.*$", message = "Avatar URL must be a valid HTTP or HTTPS URL")
    private String avatar;

    private Set<String> roles;

}
