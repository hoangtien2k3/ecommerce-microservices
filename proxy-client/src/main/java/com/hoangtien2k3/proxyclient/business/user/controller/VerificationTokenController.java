package com.hoangtien2k3.proxyclient.business.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoangtien2k3.proxyclient.business.user.model.VerificationTokenDto;
import com.hoangtien2k3.proxyclient.business.user.model.response.VerificationUserTokenServiceCollectionDtoResponse;
import com.hoangtien2k3.proxyclient.business.user.service.VerificationTokenClientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/verificationTokens")
@RequiredArgsConstructor
public class VerificationTokenController {

    private final VerificationTokenClientService verificationTokenClientService;

    @GetMapping
    public ResponseEntity<VerificationUserTokenServiceCollectionDtoResponse> findAll() {
        return ResponseEntity.ok(this.verificationTokenClientService.findAll().getBody());
    }

    @GetMapping("/{verificationTokenId}")
    public ResponseEntity<VerificationTokenDto> findById(@PathVariable("verificationTokenId") final String verificationTokenId) {
        return ResponseEntity.ok(this.verificationTokenClientService.findById(verificationTokenId).getBody());
    }

    @PostMapping
    public ResponseEntity<VerificationTokenDto> save(@RequestBody final VerificationTokenDto verificationTokenDto) {
        return ResponseEntity.ok(this.verificationTokenClientService.save(verificationTokenDto).getBody());
    }

    @PutMapping
    public ResponseEntity<VerificationTokenDto> update(@RequestBody final VerificationTokenDto verificationTokenDto) {
        return ResponseEntity.ok(this.verificationTokenClientService.update(verificationTokenDto).getBody());
    }

    @PutMapping("/{verificationTokenId}")
    public ResponseEntity<VerificationTokenDto> update(@PathVariable("verificationTokenId") final String verificationTokenId, @RequestBody final VerificationTokenDto verificationTokenDto) {
        return ResponseEntity.ok(this.verificationTokenClientService.update(verificationTokenDto).getBody());
    }

    @DeleteMapping("/{verificationTokenId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("verificationTokenId") final String verificationTokenId) {
        return ResponseEntity.ok(this.verificationTokenClientService.deleteById(verificationTokenId).getBody());
    }

}