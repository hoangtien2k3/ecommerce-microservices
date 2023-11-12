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

import com.hoangtien2k3.proxyclient.business.user.model.AddressDto;
import com.hoangtien2k3.proxyclient.business.user.model.response.AddressUserServiceCollectionDtoResponse;
import com.hoangtien2k3.proxyclient.business.user.service.AddressClientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressClientService addressClientService;

    @GetMapping
    public ResponseEntity<AddressUserServiceCollectionDtoResponse> findAll() {
        return ResponseEntity.ok(this.addressClientService.findAll().getBody());
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressDto> findById(@PathVariable("addressId") final String addressId) {
        return ResponseEntity.ok(this.addressClientService.findById(addressId).getBody());
    }

    @PostMapping
    public ResponseEntity<AddressDto> save(@RequestBody final AddressDto addressDto) {
        return ResponseEntity.ok(this.addressClientService.save(addressDto).getBody());
    }

    @PutMapping
    public ResponseEntity<AddressDto> update(@RequestBody final AddressDto addressDto) {
        return ResponseEntity.ok(this.addressClientService.update(addressDto).getBody());
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressDto> update(@PathVariable("addressId") final String addressId, @RequestBody final AddressDto addressDto) {
        return ResponseEntity.ok(this.addressClientService.update(addressDto).getBody());
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("addressId") final String addressId) {
        return ResponseEntity.ok(this.addressClientService.deleteById(addressId).getBody());
    }

}