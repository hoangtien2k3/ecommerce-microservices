package com.ecommerce.orderservice.helper;

import com.ecommerce.orderservice.dto.order.CartDto;
import com.ecommerce.orderservice.dto.order.OrderDto;
import com.ecommerce.orderservice.entity.Cart;
import com.ecommerce.orderservice.entity.Order;

public interface OrderMappingHelper {
    static OrderDto map(Order order) {
        if (order == null) return null;
        return OrderDto.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getOrderDate())
                .orderDesc(order.getOrderDesc())
                .orderFee(order.getOrderFee())
                .promotionId(order.getPromotionId())
                .discountAmount(order.getDiscountAmount())
                .productId(order.getProductId())
                .cartDto(mapCart(order.getCart()))
                .build();
    }

    static Order map(final OrderDto orderDto) {
        if (orderDto == null) return null;
        return Order.builder()
                .orderId(orderDto.getOrderId())
                .orderDate(orderDto.getOrderDate())
                .orderDesc(orderDto.getOrderDesc())
                .orderFee(orderDto.getOrderFee())
                .promotionId(orderDto.getPromotionId())
                .discountAmount(orderDto.getDiscountAmount())
                .productId(orderDto.getProductId())
                .cart(mapCart(orderDto.getCartDto()))
                .build();
    }

    private static CartDto mapCart(Cart cart) {
        if (cart == null) return null;
        return CartDto.builder()
                .cartId(cart.getCartId())
                .userId(cart.getUserId())
                .build();
    }

    // Orders can be created without a cart; only link one when an id is given.
    private static Cart mapCart(CartDto cartDto) {
        if (cartDto == null || cartDto.getCartId() == null) return null;
        return Cart.builder()
                .cartId(cartDto.getCartId())
                .userId(cartDto.getUserId())
                .build();
    }
}
