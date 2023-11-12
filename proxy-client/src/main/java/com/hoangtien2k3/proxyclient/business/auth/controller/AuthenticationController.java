package com.hoangtien2k3.proxyclient.business.auth.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoangtien2k3.proxyclient.business.auth.model.request.AuthenticationRequest;
import com.hoangtien2k3.proxyclient.business.auth.model.response.AuthenticationResponse;
import com.hoangtien2k3.proxyclient.business.auth.service.AuthenticationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/authenticate")
@Slf4j
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody
            @NotNull(message = "")
            @Valid final AuthenticationRequest authenticationRequest) {
        log.info("**Authentication controller, proceed with the request*\n");
        return ResponseEntity.ok(this.authenticationService.authenticate(authenticationRequest));
    }

    @GetMapping("/jwt/{jwt}")
    public ResponseEntity<Boolean> authenticate(@PathVariable("jwt") final String jwt) {
        log.info("**Authentication controller, proceed with the request*\n");
        return ResponseEntity.ok(this.authenticationService.authenticate(jwt));
    }

}