package com.hoangtien2k3.proxyclient.business.favourite.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoangtien2k3.proxyclient.business.favourite.model.FavouriteDto;
import com.hoangtien2k3.proxyclient.business.favourite.model.FavouriteId;
import com.hoangtien2k3.proxyclient.business.favourite.model.response.FavouriteFavouriteServiceCollectionDtoResponse;
import com.hoangtien2k3.proxyclient.business.favourite.service.FavouriteClientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/favourites")
@RequiredArgsConstructor
public class FavouriteController {

    private final FavouriteClientService favouriteClientService;

    @GetMapping
    public ResponseEntity<FavouriteFavouriteServiceCollectionDtoResponse> findAll() {
        return ResponseEntity.ok(this.favouriteClientService.findAll().getBody());
    }

    @GetMapping("/{userId}/{productId}/{likeDate}")
    public ResponseEntity<FavouriteDto> findById(@PathVariable("userId") final String userId,
                                                 @PathVariable("productId") final String productId,
                                                 @PathVariable("likeDate") final String likeDate) {
        return ResponseEntity.ok(this.favouriteClientService.findById(userId, productId, likeDate).getBody());
    }

    @GetMapping("/find")
    public ResponseEntity<FavouriteDto> findById(@RequestBody final FavouriteId favouriteId) {
        return ResponseEntity.ok(this.favouriteClientService.findById(favouriteId).getBody());
    }

    @PostMapping
    public ResponseEntity<FavouriteDto> save(@RequestBody final FavouriteDto favouriteDto) {
        return ResponseEntity.ok(this.favouriteClientService.save(favouriteDto).getBody());
    }

    @PutMapping
    public ResponseEntity<FavouriteDto> update(@RequestBody final FavouriteDto favouriteDto) {
        return ResponseEntity.ok(this.favouriteClientService.update(favouriteDto).getBody());
    }

    @DeleteMapping("/{userId}/{productId}/{likeDate}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("userId") final String userId,
                                              @PathVariable("productId") final String productId,
                                              @PathVariable("likeDate") final String likeDate) {
        this.favouriteClientService.deleteById(userId, productId, likeDate).getBody();
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteById(@RequestBody final FavouriteId favouriteId) {
        this.favouriteClientService.deleteById(favouriteId).getBody();
        return ResponseEntity.ok(true);
    }

}