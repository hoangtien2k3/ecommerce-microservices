package com.hoangtien2k3.orderservice.service.impl;

import com.hoangtien2k3.orderservice.dto.order.CartDto;
import com.hoangtien2k3.orderservice.entity.Cart;
import com.hoangtien2k3.orderservice.exception.wrapper.CartNotFoundException;
import com.hoangtien2k3.orderservice.helper.CartMappingHelper;
import com.hoangtien2k3.orderservice.repository.CartRepository;
import com.hoangtien2k3.orderservice.repository.OrderRepository;
import com.hoangtien2k3.orderservice.security.JwtTokenFilter;
import com.hoangtien2k3.orderservice.service.CartService;
import com.hoangtien2k3.orderservice.service.CallAPI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Slf4j
@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private final CartRepository cartRepository;

    @Autowired
    private final OrderServiceImpl orderService;

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    private final CallAPI callAPI;


    @Override
    public Mono<List<CartDto>> findAll() {
        log.info("CartDto List, service; fetch all carts");

        return Mono.fromSupplier(() -> cartRepository.findAll()
                        .stream()
                        .map(CartMappingHelper::map)
                        .toList()
                )
                .flatMap(cartDtos -> Flux.fromIterable(cartDtos)
                        .flatMap(cartDto ->
                                callAPI.receiverUserDto(cartDto.getUserDto().getId(), JwtTokenFilter.getTokenFromRequest())
                                        .map(userDto -> {
                                            cartDto.setUserDto(userDto);
                                            return cartDto;
                                        })
                                        .onErrorResume(throwable -> {
                                            log.error("Error fetching user info: {}", throwable.getMessage());
                                            return Mono.just(cartDto);
                                        })
                        ).collectList()
                );
    }

    @Override
    public Mono<Page<CartDto>> findAll(int page, int size, String sortBy, String sortOrder) {
        log.info("CartDto List, service; fetch all carts with paging and sorting");
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return Mono.fromSupplier(() -> cartRepository
                        .findAll(pageable)
                        .map(CartMappingHelper::map)
                )
                .flatMap(cartDtos -> Flux.fromIterable(cartDtos)
                        .flatMap(cartDto ->
                                callAPI.receiverUserDto(cartDto.getUserDto().getId(), JwtTokenFilter.getTokenFromRequest())
                                        .map(userDto -> {
                                            cartDto.setUserDto(userDto);
                                            return cartDto;
                                        })
                                        .onErrorResume(throwable -> {
                                            log.error("Error fetching user info: {}", throwable.getMessage());
                                            return Mono.just(cartDto);
                                        })
                        )
                        .collectList()
                        .map(resultList -> new PageImpl<>(resultList, pageable, resultList.size()))
                );
    }

    @Override
    public Mono<CartDto> findById(Integer cartId) {
        log.info("CartDto, service; fetch cart by id");

        return Mono.fromSupplier(() -> cartRepository.findById(cartId)
                        .map(CartMappingHelper::map)
                        .orElseThrow(() -> new CartNotFoundException(String.format("Cart with id: %d not found", cartId)))
                )
                .flatMap(cartDto ->
                        callAPI.receiverUserDto(cartDto.getUserDto().getId(), JwtTokenFilter.getTokenFromRequest())
                                .map(userDto -> {
                                    cartDto.setUserDto(userDto);
                                    return cartDto;
                                })
                                .onErrorResume(throwable -> {
                                    log.error("Error fetching user info: {}", throwable.getMessage());
                                    return Mono.just(cartDto);
                                })
                );
    }

    @Override
    public Mono<CartDto> save(final CartDto cartDto) {
        log.info("CartDto, service; save cart");
        return Mono.fromSupplier(() -> cartRepository.save(modelMapper.map(cartDto, Cart.class)))
                .map(savedCart -> modelMapper.map(savedCart, CartDto.class));
    }

    @Override
    public Mono<CartDto> update(final CartDto cartDto) {
        log.info("CartDto, service; update cart");
        return Mono.fromSupplier(() -> cartRepository.save(CartMappingHelper.map(cartDto)))
                .map(CartMappingHelper::map);
    }

    @Override
    public Mono<CartDto> update(final Integer cartId, final CartDto cartDto) {
        log.info("CartDto, service; update cart with cartId");
        return findById(cartId).flatMap(existingCartDto -> {
                    modelMapper.map(cartDto, existingCartDto);
                    return Mono.fromSupplier(() -> cartRepository.save(CartMappingHelper.map(existingCartDto)))
                            .map(CartMappingHelper::map);
                })
                .switchIfEmpty(Mono.error(new CartNotFoundException("Cart with id " + cartId + " not found")));
    }

    @Override
    public Mono<Void> deleteById(final Integer cartId) {
        log.info("Void, service; delete cart by id");
        cartRepository.findById(cartId)
                .ifPresent(cart -> {
                    orderRepository.deleteAllByCart(cart);
                    cartRepository.deleteById(cartId);
                });
        return Mono.empty();
    }

}
