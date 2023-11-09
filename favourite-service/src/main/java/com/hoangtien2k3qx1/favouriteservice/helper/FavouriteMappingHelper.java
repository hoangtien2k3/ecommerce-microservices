package com.hoangtien2k3qx1.favouriteservice.helper;

import com.hoangtien2k3qx1.favouriteservice.dto.FavouriteDto;
import com.hoangtien2k3qx1.favouriteservice.dto.ProductDto;
import com.hoangtien2k3qx1.favouriteservice.dto.UserDto;
import com.hoangtien2k3qx1.favouriteservice.entity.Favourite;

public class FavouriteMappingHelper {

    // mapping Favourite -> FavouriteDto
    public static FavouriteDto map(final Favourite favourite) {
        return FavouriteDto.builder()
                .userId(favourite.userId())
                .productId(favourite.productId())
                .likeDate(favourite.likeDate())
                .userDto(
                        UserDto.builder()
                                .userId(favourite.userId())
                                .build())
                .productDto(
                        ProductDto.builder()
                                .productId(favourite.productId())
                                .build())
                .build();
    }

    // mapping FavouriteDto -> Favourites
    public static Favourite map(final FavouriteDto favouriteDto) {
        return Favourite.builder()
                .userId(favouriteDto.getUserId())
                .productId(favouriteDto.getProductId())
                .likeDate(favouriteDto.getLikeDate())
                .build();
    }



}


