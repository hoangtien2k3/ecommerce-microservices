package com.ecommerce.shippingservice.service;

import com.ecommerce.shippingservice.domain.id.OrderItemId;
import com.ecommerce.shippingservice.dto.OrderItemDto;

import java.util.List;

public interface OrderItemService {

    List<OrderItemDto> findAll();
    OrderItemDto findById(final OrderItemId orderItemId);
    OrderItemDto save(final OrderItemDto orderItemDto);
    OrderItemDto update(final OrderItemDto orderItemDto);
    void deleteById(final OrderItemId orderItemId);

}
