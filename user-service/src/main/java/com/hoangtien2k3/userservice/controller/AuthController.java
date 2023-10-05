package com.hoangtien2k3.userservice.controller;

import com.hoangtien2k3.userservice.dto.request.SignInForm;
import com.hoangtien2k3.userservice.dto.request.SignUpForm;
import com.hoangtien2k3.userservice.dto.request.TokenValidationRequest;
import com.hoangtien2k3.userservice.dto.request.TokenValidationResponse;
import com.hoangtien2k3.userservice.dto.response.JwtResponse;
import com.hoangtien2k3.userservice.dto.response.ResponseMessage;
import com.hoangtien2k3.userservice.security.jwt.JwtProvider;
import com.hoangtien2k3.userservice.service.impl.UserServiceImpl;
import com.hoangtien2k3.userservice.service.validate.TokenValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserServiceImpl userService;
    private final TokenValidate tokenValidate;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    public AuthController(UserServiceImpl userService, TokenValidate tokenValidate) {
        this.userService = userService;
        this.tokenValidate = tokenValidate;
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
                    JwtResponse errorResponse = new JwtResponse(null,null, null, null, null);
                    return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED));
                });
    }


    @PostMapping("/refresh")
    public Mono<ResponseEntity<JwtResponse>> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        return userService.refreshToken(refreshToken)
                .map(newAccessToken -> {
                    JwtResponse jwtResponse = new JwtResponse(newAccessToken, null, null, null, null);
                    return ResponseEntity.ok(jwtResponse);
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }


    @PostMapping("/validateToken")
    public Boolean validateToken(@RequestBody TokenValidationRequest validationRequest) {

        // @RequestHeader(name = "Authorization") String authorizationToken

        String accessToken = validationRequest.getAccessToken();
        if (TokenValidate.validateToken(accessToken)) {
            // Token hợp lệ, có thể thực hiện các xử lý hoặc trả về thông báo thành công
            return ResponseEntity.ok(new TokenValidationResponse("Valid token")).hasBody();
        } else {
            // Token không hợp lệ hoặc hết hạn
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenValidationResponse("Invalid token")).hasBody();
        }

    }

}
