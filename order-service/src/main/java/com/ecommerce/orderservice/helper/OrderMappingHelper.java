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
                .productId(order.getProductId())
                .cartDto(CartDto.builder()
                        .cartId(order.getCart().getCartId())
                        .userId(order.getCart().getUserId())
                        .build())
                .build();
    }

    static Order map(final OrderDto orderDto) {
        if (orderDto == null) return null;
        return Order.builder()
                .orderId(orderDto.getOrderId())
                .orderDate(orderDto.getOrderDate())
                .orderDesc(orderDto.getOrderDesc())
                .orderFee(orderDto.getOrderFee())
                .productId(orderDto.getProductId())
                .cart(Cart.builder()
                        .cartId(orderDto.getCartDto().getCartId())
                        .userId(orderDto.getCartDto().getUserId())
                        .build())
                .build();
    }
}
