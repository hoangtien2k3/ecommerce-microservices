package com.hoangtien2k3.proxyclient.business.favourite.service;

import javax.validation.Valid;
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

import com.hoangtien2k3.proxyclient.business.favourite.model.FavouriteDto;
import com.hoangtien2k3.proxyclient.business.favourite.model.FavouriteId;
import com.hoangtien2k3.proxyclient.business.favourite.model.response.FavouriteFavouriteServiceCollectionDtoResponse;

@FeignClient(name = "FAVOURITE-SERVICE",
        contextId = "favouriteClientService",
        path = "/favourite-service/api/favourites"
)
@Service
public interface FavouriteClientService {

    @GetMapping
    ResponseEntity<FavouriteFavouriteServiceCollectionDtoResponse> findAll();

    @GetMapping("/{userId}/{productId}/{likeDate}")
    public ResponseEntity<FavouriteDto> findById(@PathVariable("userId") final String userId,
                                                 @PathVariable("productId") final String productId,
                                                 @PathVariable("likeDate") final String likeDate);

    @GetMapping("/find")
    public ResponseEntity<FavouriteDto> findById(@RequestBody
                                                 @NotNull(message = "Input must not be NULL")
                                                 @Valid final FavouriteId favouriteId);

    @PostMapping
    public ResponseEntity<FavouriteDto> save(@RequestBody
                                             @NotNull(message = "Input must not be NULL")
                                             @Valid final FavouriteDto favouriteDto);

    @PutMapping
    public ResponseEntity<FavouriteDto> update(@RequestBody
                                               @NotNull(message = "Input must not be NULL")
                                               @Valid final FavouriteDto favouriteDto);

    @DeleteMapping("/{userId}/{productId}/{likeDate}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("userId") final String userId,
                                              @PathVariable("productId") final String productId,
                                              @PathVariable("likeDate") final String likeDate);

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteById(@RequestBody
                                              @NotNull(message = "Input must not be NULL")
                                              @Valid final FavouriteId favouriteId);

}