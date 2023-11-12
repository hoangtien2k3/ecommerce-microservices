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

import com.hoangtien2k3.proxyclient.business.user.model.AddressDto;
import com.hoangtien2k3.proxyclient.business.user.model.response.AddressUserServiceCollectionDtoResponse;

@FeignClient(name = "USER-SERVICE",
        contextId = "addressClientService",
        path = "/user-service/api/address",
        decode404 = true
)
@Service
public interface AddressClientService {

    @GetMapping
    ResponseEntity<AddressUserServiceCollectionDtoResponse> findAll();

    @GetMapping("/{addressId}")
    ResponseEntity<AddressDto> findById(@PathVariable("addressId")
                                        @NotBlank(message = "Input must not blank")
                                        @Valid final String addressId);

    @PostMapping
    ResponseEntity<AddressDto> save(@RequestBody
                                    @NotNull(message = "Input must not NULL")
                                    @Valid final AddressDto addressDto);

    @PutMapping
    ResponseEntity<AddressDto> update(@RequestBody
                                      @NotNull(message = "Input must not NULL")
                                      @Valid final AddressDto addressDto);

    @PutMapping("/{addressId}")
    ResponseEntity<AddressDto> update(@PathVariable("addressId")
                                      @NotBlank(message = "Input must not blank") final String addressId,
                                      @RequestBody
                                      @NotNull(message = "Input must not NULL")
                                      @Valid final AddressDto addressDto);

    @DeleteMapping("/{addressId}")
    ResponseEntity<Boolean> deleteById(@PathVariable("addressId")
                                       @NotBlank(message = "Input must not blank")
                                       @Valid final String addressId);

}