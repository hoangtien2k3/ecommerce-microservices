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

import com.hoangtien2k3.proxyclient.business.user.model.CredentialDto;
import com.hoangtien2k3.proxyclient.business.user.model.response.CredentialUserServiceCollectionDtoResponse;
import com.hoangtien2k3.proxyclient.business.user.service.CredentialClientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/credentials")
@RequiredArgsConstructor
public class CredentialController {

    private final CredentialClientService credentialClientService;

    @GetMapping
    public ResponseEntity<CredentialUserServiceCollectionDtoResponse> findAll() {
        return ResponseEntity.ok(this.credentialClientService.findAll().getBody());
    }

    @GetMapping("/{credentialId}")
    public ResponseEntity<CredentialDto> findById(@PathVariable("credentialId") final String credentialId) {
        return ResponseEntity.ok(this.credentialClientService.findById(credentialId).getBody());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<CredentialDto> findByCredentialname(@PathVariable("username") final String username) {
        return ResponseEntity.ok(this.credentialClientService.findByUsername(username).getBody());
    }

    @PostMapping
    public ResponseEntity<CredentialDto> save(@RequestBody final CredentialDto credentialDto) {
        return ResponseEntity.ok(this.credentialClientService.save(credentialDto).getBody());
    }

    @PutMapping
    public ResponseEntity<CredentialDto> update(@RequestBody final CredentialDto credentialDto) {
        return ResponseEntity.ok(this.credentialClientService.update(credentialDto).getBody());
    }

    @PutMapping("/{credentialId}")
    public ResponseEntity<CredentialDto> update(@PathVariable("credentialId") final String credentialId, @RequestBody final CredentialDto credentialDto) {
        return ResponseEntity.ok(this.credentialClientService.update(credentialDto).getBody());
    }

    @DeleteMapping("/{credentialId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("credentialId") final String credentialId) {
        return ResponseEntity.ok(this.credentialClientService.deleteById(credentialId).getBody());
    }

}