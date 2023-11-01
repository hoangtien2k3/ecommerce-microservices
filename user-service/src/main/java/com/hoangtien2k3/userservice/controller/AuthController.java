package com.hoangtien2k3.userservice.controller;

import com.hoangtien2k3.userservice.dto.request.SignInForm;
import com.hoangtien2k3.userservice.dto.request.SignUpForm;
import com.hoangtien2k3.userservice.dto.request.TokenValidationResponse;
import com.hoangtien2k3.userservice.dto.response.JwtResponse;
import com.hoangtien2k3.userservice.dto.response.ResponseMessage;
import com.hoangtien2k3.userservice.security.jwt.JwtProvider;
import com.hoangtien2k3.userservice.security.validate.AuthorityTokenUtil;
import com.hoangtien2k3.userservice.service.impl.UserServiceImpl;
import com.hoangtien2k3.userservice.security.validate.TokenValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping("/signup")
    public Mono<ResponseEntity<ResponseMessage>> register(@Valid @RequestBody SignUpForm signUpForm) {
        return userService.registerUser(signUpForm)
                .flatMap(user -> Mono.just(new ResponseEntity<>(
                        new ResponseMessage("User: " + signUpForm.getUsername() + " create successfully."),
                        HttpStatus.OK))
                )
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

    @GetMapping("/validateToken")
    public Boolean validateToken(@RequestHeader(name = "Authorization") String authorizationToken) {
        TokenValidate validate = new TokenValidate();
        if (validate.validateToken(authorizationToken)) {
            return ResponseEntity.ok(new TokenValidationResponse("Valid token")).hasBody();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenValidationResponse("Invalid token")).hasBody();
        }
    }


    // check role token authorities
    @GetMapping("/hasAuthority")
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
