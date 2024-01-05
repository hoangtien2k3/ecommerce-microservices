package com.hoangtien2k3.orderservice.service.impl;

import com.hoangtien2k3.orderservice.dto.CartDto;
import com.hoangtien2k3.orderservice.dto.OrderDto;
import com.hoangtien2k3.orderservice.entity.Cart;
import com.hoangtien2k3.orderservice.entity.Order;
import com.hoangtien2k3.orderservice.exception.wrapper.CartNotFoundException;
import com.hoangtien2k3.orderservice.exception.wrapper.OrderNotFoundException;
import com.hoangtien2k3.orderservice.helper.CartMappingHelper;
import com.hoangtien2k3.orderservice.helper.OrderMappingHelper;
import com.hoangtien2k3.orderservice.repository.OrderRepository;
import com.hoangtien2k3.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final ModelMapper modelMapper;

    @Override
    public Mono<List<OrderDto>> findAll() {
        log.info("OrderDto List, service; fetch all orders");
        return Mono.fromSupplier(() -> orderRepository.findAll()
                        .stream()
                        .map(OrderMappingHelper::map)
                        .toList()
                );
    }

    @Override
    public Mono<Page<OrderDto>> findAll(int page, int size, String sortBy, String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return Mono.fromSupplier(() -> orderRepository.findAll(pageable)
                .map(OrderMappingHelper::map)
        );
    }

    @Override
    public Mono<OrderDto> findById(Integer orderId) {
        log.info("OrderDto, service; fetch order by id");
        return Mono.fromSupplier(() -> orderRepository.findById(orderId)
                .map(OrderMappingHelper::map)
                .orElseThrow(() -> new CartNotFoundException(String.format("Cart with id: %d not found", orderId)))
        );
    }

    @Override
    public Mono<OrderDto> save(final OrderDto orderDto) {
        log.info("OrderDto, service; save order");
        return Mono.fromSupplier(() -> OrderMappingHelper.map(orderRepository.save(OrderMappingHelper.map(orderDto))))
                .onErrorResume(throwable -> {
                    log.error("Error saving order: {}", throwable.getMessage());
                    return Mono.error(throwable);
                });
    }

    @Override
    public Mono<OrderDto> update(final OrderDto orderDto) {
        log.info("OrderDto, service; update order");
        return Mono.fromSupplier(() -> orderRepository.save(OrderMappingHelper.map(orderDto)))
                .map(OrderMappingHelper::map);
    }

    @Override
    public Mono<OrderDto> update(final Integer orderId, final OrderDto orderDto) {
        log.info("OrderDto, service; update order with orderId");
        return findById(orderId).flatMap(existingOrderDto -> {
                    modelMapper.map(orderDto, existingOrderDto);
                    return Mono.fromSupplier(() -> orderRepository.save(OrderMappingHelper.map(existingOrderDto)))
                            .map(OrderMappingHelper::map);
                })
                .switchIfEmpty(Mono.error(new CartNotFoundException("Cart with id " + orderId + " not found")));
    }

    @Override
    public Mono<Void> deleteById(final Integer orderId) {
        log.info("Void, service; delete order by id");
        return Mono.fromRunnable(() -> orderRepository.deleteById(orderId));
    }

}
