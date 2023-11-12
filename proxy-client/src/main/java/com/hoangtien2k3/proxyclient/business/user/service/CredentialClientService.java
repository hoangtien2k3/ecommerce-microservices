package com.hoangtien2k3.proxyclient.business.user.service;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.hoangtien2k3.proxyclient.business.user.model.CredentialDto;
import com.hoangtien2k3.proxyclient.business.user.model.response.CredentialUserServiceCollectionDtoResponse;

@FeignClient(name = "USER-SERVICE",
        contextId = "credentialClientService",
        path = "/user-service/api/credentials",
        decode404 = true
)
@Service
public interface CredentialClientService {

    @GetMapping
    ResponseEntity<CredentialUserServiceCollectionDtoResponse> findAll();

    @GetMapping("/{credentialId}")
    ResponseEntity<CredentialDto> findById(@PathVariable("credentialId")
                                           @NotBlank(message = "Input must not blank")
                                           @Valid final String credentialId);

    @GetMapping("/username/{username}")
    ResponseEntity<CredentialDto> findByUsername(@PathVariable("username")
                                                 @NotBlank(message = "Input must not blank")
                                                 @Valid final String username);

    @PostMapping
    ResponseEntity<CredentialDto> save(@RequestBody
                                       @NotNull(message = "Input must not NULL")
                                       @Valid final CredentialDto credentialDto);

    @PutMapping
    ResponseEntity<CredentialDto> update(@RequestBody
                                         @NotNull(message = "Input must not NULL")
                                         @Valid final CredentialDto credentialDto);

    @PutMapping("/{credentialId}")
    ResponseEntity<CredentialDto> update(@PathVariable("credentialId")
                                         @NotBlank(message = "Input must not blank") final String credentialId,
                                         @RequestBody
                                         @NotNull(message = "Input must not NUL")
                                         @Valid final CredentialDto credentialDto);

    @DeleteMapping("/{credentialId}")
    ResponseEntity<Boolean> deleteById(@PathVariable("credentialId")
                                       @NotBlank(message = "Input must not blank")
                                       @Valid final String credentialId);

}