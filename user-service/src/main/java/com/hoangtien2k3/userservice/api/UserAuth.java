package com.hoangtien2k3.userservice.api;

import com.hoangtien2k3.userservice.model.dto.request.Login;
import com.hoangtien2k3.userservice.model.dto.request.SignUp;
import com.hoangtien2k3.userservice.model.dto.response.TokenValidationResponse;
import com.hoangtien2k3.userservice.model.dto.response.InformationMessage;
import com.hoangtien2k3.userservice.model.dto.response.JwtResponseMessage;
import com.hoangtien2k3.userservice.model.dto.response.ResponseMessage;
import com.hoangtien2k3.userservice.security.jwt.JwtProvider;
import com.hoangtien2k3.userservice.security.validate.AuthorityTokenUtil;
import com.hoangtien2k3.userservice.service.EmailService;
import com.hoangtien2k3.userservice.service.UserService;
import com.hoangtien2k3.userservice.security.validate.TokenValidate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Api(value = "User Authentication API",
        description = "APIs for user registration, login, and authentication"
)
public class UserAuth {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Autowired
    private EmailService emailService;

    @Autowired
    public UserAuth(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @ApiOperation(value = "Register a new user", notes = "Registers a new user with the provided details.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User created successfully", response = ResponseMessage.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ResponseMessage.class)
    })
    @PostMapping({"/signup", "/register"})
    public Mono<ResponseMessage> register(@Valid @RequestBody SignUp signUp) {
        return userService.register(signUp)
                .map(user -> new ResponseMessage("Create user: " + signUp.getUsername() + " successfully."))
                .onErrorResume(error -> Mono.just(new ResponseMessage("Error occurred while creating the account.")));
    }

    @ApiOperation(value = "User login", notes = "Logs in a user with the provided credentials.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Login successful", response = JwtResponseMessage.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ResponseEntity.class)
    })
    @PostMapping({"/signin", "/login"})
    public Mono<ResponseEntity<JwtResponseMessage>> login(@Valid @RequestBody Login signInForm) {
        return userService.login(signInForm)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    JwtResponseMessage errorjwtResponseMessage = new JwtResponseMessage(
                            null,
                            null,
                            new InformationMessage()
                    );
                    return Mono.just(new ResponseEntity<>(errorjwtResponseMessage, HttpStatus.INTERNAL_SERVER_ERROR));
                });
    }

    @ApiOperation(value = "User logout", notes = "Logs out the authenticated user.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Logged out successfully", response = String.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ResponseEntity.class)
    })
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public Mono<ResponseEntity<String>> logout() {
        log.info("Logout endpoint called");
        return userService.logout()
                .then(Mono.just(new ResponseEntity<>("Logged out successfully.", HttpStatus.OK)))
                .onErrorResume(error -> {
                    log.error("Logout failed", error);
                    return Mono.just(new ResponseEntity<>("Logout failed.", HttpStatus.BAD_REQUEST));
                });
    }


//    @PostMapping("/reset-password")
//    public Mono<ResponseEntity<String>> resetPassword(@RequestParam("token") String token, @RequestBody ResetPasswordRequest resetPasswordRequest) {
//        if (isValidToken(token)) {
//            // Token hợp lệ, đặt mật khẩu mới và cập nhật trong cơ sở dữ liệu
//            updatePassword(userEmail, resetPasswordRequest.getNewPassword());
//            return Mono.just(ResponseEntity.ok("Password reset successful"));
//        } else {
//            // Token không hợp lệ
//            return Mono.just(ResponseEntity.badRequest().body("Invalid token"));
//        }
//    }


//    @PostMapping({"/refresh", "/refresh-token"})
//    public Mono<ResponseEntity<JwtResponseMessage>> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
//        return userService.refreshToken(refreshToken)
//                .map(newAccessToken -> {
//                    JwtResponseMessage jwtResponseMessage = new JwtResponseMessage(newAccessToken, null, null);
//                    return ResponseEntity.ok(jwtResponseMessage);
//                })
//                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
//    }

    @ApiOperation(value = "Validate JWT token", notes = "Validates the provided JWT token.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Token is valid", response = Boolean.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = TokenValidationResponse.class)
    })
    @GetMapping({"/validateToken", "/validate-token"})
    public Boolean validateToken(@RequestHeader(name = "Authorization") String authorizationToken) {
        TokenValidate validate = new TokenValidate();
        if (validate.validateToken(authorizationToken)) {
            return ResponseEntity.ok(new TokenValidationResponse("Valid token")).hasBody();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenValidationResponse("Invalid token")).hasBody();
        }
    }

    @ApiOperation(value = "Check user authority", notes = "Checks if the user has the specified authority.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Role access API", response = Boolean.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = TokenValidationResponse.class)
    })
    @GetMapping({"/hasAuthority", "/authorization"})
    public Boolean getAuthority(@RequestHeader(name = "Authorization") String authorizationToken,
                                String requiredRole) {
        AuthorityTokenUtil authorityTokenUtil = new AuthorityTokenUtil();
        List<String> authorities = authorityTokenUtil.checkPermission(authorizationToken);

        if (authorities.contains(requiredRole)) {
            return ResponseEntity.ok(new TokenValidationResponse("Role access api")).hasBody();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenValidationResponse("Invalid token")).hasBody();
        }
    }

}
