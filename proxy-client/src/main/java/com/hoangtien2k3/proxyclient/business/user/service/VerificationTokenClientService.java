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

import com.hoangtien2k3.proxyclient.business.user.model.VerificationTokenDto;
import com.hoangtien2k3.proxyclient.business.user.model.response.VerificationUserTokenServiceCollectionDtoResponse;

@FeignClient(name = "USER-SERVICE",
        contextId = "verificationTokenClientService",
        path = "/user-service/api/verificationTokens",
        decode404 = true
)
@Service
public interface VerificationTokenClientService {

    @GetMapping
    ResponseEntity<VerificationUserTokenServiceCollectionDtoResponse> findAll();

    @GetMapping("/{verificationTokenId}")
    ResponseEntity<VerificationTokenDto> findById(@PathVariable("verificationTokenId")
                                                  @NotBlank(message = "Input must not blank")
                                                  @Valid final String verificationTokenId);

    @PostMapping
    ResponseEntity<VerificationTokenDto> save(@RequestBody
                                              @NotNull(message = "Input must not NULL")
                                              @Valid final VerificationTokenDto verificationTokenDto);

    @PutMapping
    ResponseEntity<VerificationTokenDto> update(@RequestBody
                                                @NotNull(message = "Input must not NULL")
                                                @Valid final VerificationTokenDto verificationTokenDto);

    @PutMapping("/{verificationTokenId}")
    ResponseEntity<VerificationTokenDto> update(@PathVariable("verificationTokenId")
                                                @NotBlank(message = "Input must not blank!**") final String verificationTokenId,
                                                @RequestBody
                                                @NotNull(message = "Input must not NULL")
                                                @Valid final VerificationTokenDto verificationTokenDto);

    @DeleteMapping("/{verificationTokenId}")
    ResponseEntity<Boolean> deleteById(@PathVariable("verificationTokenId")
                                       @NotBlank(message = "Input must not blank")
                                       @Valid final String verificationTokenId);

}