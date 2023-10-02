package com.hoangtien2k3.shippingservice.service;

import com.hoangtien2k3.shippingservice.domain.id.OrderItemId;
import com.hoangtien2k3.shippingservice.dto.OrderItemDto;

import java.util.List;

public interface OrderItemService {

    List<OrderItemDto> findAll();
    OrderItemDto findById(final OrderItemId orderItemId);
    OrderItemDto save(final OrderItemDto orderItemDto);
    OrderItemDto update(final OrderItemDto orderItemDto);
    void deleteById(final OrderItemId orderItemId);

}