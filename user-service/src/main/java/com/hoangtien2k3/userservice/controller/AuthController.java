package com.hoangtien2k3.userservice.controller;

import com.hoangtien2k3.userservice.dto.request.SignInForm;
import com.hoangtien2k3.userservice.dto.request.SignUpForm;
import com.hoangtien2k3.userservice.dto.request.TokenValidationRequest;
import com.hoangtien2k3.userservice.dto.request.TokenValidationResponse;
import com.hoangtien2k3.userservice.dto.response.JwtResponse;
import com.hoangtien2k3.userservice.dto.response.ResponseMessage;
import com.hoangtien2k3.userservice.service.TokenValidationService;
import com.hoangtien2k3.userservice.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserServiceImpl userService;
    private final TokenValidationService tokenValidationService;

    @Autowired
    public AuthController(UserServiceImpl userService, TokenValidationService tokenValidationService) {
        this.userService = userService;
        this.tokenValidationService = tokenValidationService;
    }

    @PostMapping("/signup")
    public Mono<ResponseEntity<ResponseMessage>> register(@Valid @RequestBody SignUpForm signUpForm) {
        return userService.registerUser(signUpForm)
                .flatMap(user -> Mono.just(new ResponseEntity<>(new ResponseMessage("User: " + signUpForm.getUsername() + " create successfully."), HttpStatus.OK)))
                .onErrorResume(error -> Mono.just(new ResponseEntity<>(new ResponseMessage(error.getMessage()), HttpStatus.BAD_REQUEST)));
    }

    @PostMapping("/signin")
    public Mono<ResponseEntity<JwtResponse>> login(@Valid @RequestBody SignInForm signInForm) {
        return userService.login(signInForm)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    JwtResponse errorResponse = new JwtResponse(null, null, null, null);
                    return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED));
                });
    }

    @PostMapping("/validateToken")
    public ResponseEntity<?> validateToken(@RequestBody TokenValidationRequest validationRequest) {
        String accessToken = validationRequest.getAccessToken();

        boolean isValid = tokenValidationService.validateToken(accessToken);

        if (isValid) {
            // Token hợp lệ, có thể thực hiện các xử lý hoặc trả về thông báo thành công
            return ResponseEntity.ok(new TokenValidationResponse("Valid token"));
        } else {
            // Token không hợp lệ hoặc hết hạn
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenValidationResponse("Invalid token"));
        }
    }

}
