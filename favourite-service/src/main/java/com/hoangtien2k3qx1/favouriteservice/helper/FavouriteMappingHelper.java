package com.hoangtien2k3qx1.favouriteservice.helper;

import com.hoangtien2k3qx1.favouriteservice.dto.FavouriteDto;
import com.hoangtien2k3qx1.favouriteservice.dto.ProductDto;
import com.hoangtien2k3qx1.favouriteservice.dto.UserDto;
import com.hoangtien2k3qx1.favouriteservice.entity.Favourite;

public class FavouriteMappingHelper {

    public static FavouriteDto map(final Favourite favourite) {
        return FavouriteDto.builder()
                .userId(favourite.getUserId())
                .productId(favourite.getProductId())
                .likeDate(favourite.getLikeDate())
                .userDto(
                        UserDto.builder()
                                .userId(favourite.getUserId())
                                .build())
                .productDto(
                        ProductDto.builder()
                                .productId(favourite.getProductId())
                                .build())
                .build();
    }

    public static Favourite map(final FavouriteDto favouriteDto) {
        return Favourite.builder()
                .userId(favouriteDto.getUserId())
                .productId(favouriteDto.getProductId())
                .likeDate(favouriteDto.getLikeDate())
                .build();
    }

}


