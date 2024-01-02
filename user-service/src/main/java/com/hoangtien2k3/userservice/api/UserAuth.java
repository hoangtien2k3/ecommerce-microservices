package com.hoangtien2k3.userservice.api;

import com.hoangtien2k3.userservice.model.dto.request.LoginDTO;
import com.hoangtien2k3.userservice.model.dto.request.UserDTO;
import com.hoangtien2k3.userservice.model.dto.request.TokenValidationResponse;
import com.hoangtien2k3.userservice.model.dto.response.InformationMessage;
import com.hoangtien2k3.userservice.model.dto.response.JwtResponseMessage;
import com.hoangtien2k3.userservice.model.dto.response.ResponseMessage;
import com.hoangtien2k3.userservice.security.jwt.JwtProvider;
import com.hoangtien2k3.userservice.security.validate.AuthorityTokenUtil;
import com.hoangtien2k3.userservice.service.EmailService;
import com.hoangtien2k3.userservice.service.UserService;
import com.hoangtien2k3.userservice.security.validate.TokenValidate;
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

    @PostMapping({"/signup", "/register"})
    public Mono<ResponseMessage> register(@Valid @RequestBody UserDTO signUpForm) {
        return userService.register(signUpForm)
                .map(user ->
                        new ResponseMessage("Create user: " + signUpForm.getUsername() + " successfully.")
                )
                .onErrorResume(error ->
                        Mono.just(new ResponseMessage(error.getMessage()))
                );
    }

    @PostMapping({"/signin", "/login"})
    public Mono<ResponseEntity<JwtResponseMessage>> login(@Valid @RequestBody LoginDTO signInForm) {
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

    @GetMapping({"/hasAuthority", "/authorization"})
    public Boolean getAuthority(@RequestHeader(name = "Authorization") String authorizationToken,
                                String requiredRole) {
        AuthorityTokenUtil authorityTokenUtil = new AuthorityTokenUtil();
        List<String> authorities = authorityTokenUtil.checkPermission(authorizationToken);

        if(authorities.contains(requiredRole)) {
            return ResponseEntity.ok(new TokenValidationResponse("Role access api")).hasBody();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenValidationResponse("Invalid token")).hasBody();
        }
    }

}
