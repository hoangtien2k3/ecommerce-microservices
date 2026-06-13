package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.order.OrderDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    List<OrderDto> findAll();

    Page<OrderDto> findAll(int page, int size, String sortBy, String sortOrder);

    OrderDto findById(Integer orderId);

    OrderDto save(OrderDto orderDto);

    OrderDto update(OrderDto orderDto);

    OrderDto update(Integer orderId, OrderDto orderDto);

    void deleteById(Integer orderId);

    Boolean existsByOrderId(Integer orderId);
}
