package com.hoangtien2k3.orderservice.helper;

import com.hoangtien2k3.orderservice.entity.Cart;
import com.hoangtien2k3.orderservice.entity.Order;
import com.hoangtien2k3.orderservice.dto.CartDto;
import com.hoangtien2k3.orderservice.dto.OrderDto;

public interface OrderMappingHelper {
    static OrderDto map(Order order) {
        return OrderDto.builder()
                .orderId(order.orderId())
                .orderDate(order.orderDate())
                .orderDesc(order.orderDesc())
                .orderFee(order.orderFee())
                .cartDto(CartDto.builder()
                                .cartId(order.cart().cartId())
                                .build())
                .build();
    }

    static Order map(final OrderDto orderDto) {
        return Order.builder()
                .orderId(orderDto.getOrderId())
                .orderDate(orderDto.getOrderDate())
                .orderDesc(orderDto.getOrderDesc())
                .orderFee(orderDto.getOrderFee())
                .cart(Cart.builder()
                                .cartId(orderDto.getCartDto().getCartId())
                                .build())
                .build();
    }
}
