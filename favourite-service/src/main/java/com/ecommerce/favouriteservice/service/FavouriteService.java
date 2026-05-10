package com.ecommerce.favouriteservice.service;

import com.ecommerce.favouriteservice.dto.FavouriteDto;
import com.ecommerce.favouriteservice.entity.id.FavouriteId;

import java.util.List;

public interface FavouriteService {
    List<FavouriteDto> findAll();
    FavouriteDto findById(final FavouriteId favouriteId);
    FavouriteDto save(final FavouriteDto favouriteDto);
    FavouriteDto update(final FavouriteDto favouriteDto);
    void deleteById(final FavouriteId favouriteId);
}
