package com.hoangtien2k3.orderservice.helper;

import com.hoangtien2k3.orderservice.entity.Cart;
import com.hoangtien2k3.orderservice.dto.CartDto;
import com.hoangtien2k3.orderservice.dto.UserDto;

public interface CartMappingHelper {
    static CartDto map( Cart cart) {
        return CartDto.builder()
                .cartId(cart.cartId())
                .userId(cart.userId())
                .userDto(
                        UserDto.builder()
                                .userId(cart.userId())
                                .build())
                .build();
    }

    static Cart map(final CartDto cartDto) {
        return Cart.builder()
                .cartId(cartDto.getCartId())
                .userId(cartDto.getUserId())
                .build();
    }
}
