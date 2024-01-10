package com.hoangtien2k3.orderservice.service;

import com.hoangtien2k3.orderservice.dto.order.OrderDto;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrderService {
    Mono<List<OrderDto>> findAll();

    Mono<Page<OrderDto>> findAll(int page, int size, String sortBy, String sortOrder);

    Mono<OrderDto> findById(Integer orderId);

    Mono<OrderDto> save(final OrderDto orderDto);

    Mono<OrderDto> update(final OrderDto orderDto);

    Mono<OrderDto> update(final Integer orderId, final OrderDto orderDto);

    Mono<Void> deleteById(final Integer orderId);

    Boolean existsByOrderId(Integer orderId);
}
