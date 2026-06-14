package com.ecommerce.orderservice.service.impl;

import com.ecommerce.orderservice.dto.order.CartDto;
import com.ecommerce.orderservice.entity.Cart;
import com.ecommerce.orderservice.exception.wrapper.CartNotFoundException;
import com.ecommerce.orderservice.helper.CartMappingHelper;
import com.ecommerce.orderservice.repository.CartRepository;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.security.JwtTokenFilter;
import com.ecommerce.orderservice.service.CallAPI;
import com.ecommerce.orderservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;
    private final OrderServiceImpl orderService;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final CallAPI callAPI;

    @Override
    public List<CartDto> findAll() {
        log.info("CartDto List, service; fetch all carts");
        return cartRepository.findAll()
                .stream()
                .map(CartMappingHelper::map)
                .peek(cartDto -> {
                    try {
                        cartDto.setUserDto(callAPI.receiverUserDto(cartDto.getUserDto().getId(), JwtTokenFilter.getTokenFromRequest()));
                    } catch (Exception e) {
                        log.error("Error fetching user info: {}", e.getMessage());
                    }
                })
                .toList();
    }

    @Override
    public Page<CartDto> findAll(int page, int size, String sortBy, String sortOrder) {
        log.info("CartDto List, service; fetch all carts with paging and sorting");
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        List<CartDto> cartDtos = cartRepository.findAll(pageable)
                .stream()
                .map(CartMappingHelper::map)
                .peek(cartDto -> {
                    try {
                        cartDto.setUserDto(callAPI.receiverUserDto(cartDto.getUserDto().getId(), JwtTokenFilter.getTokenFromRequest()));
                    } catch (Exception e) {
                        log.error("Error fetching user info: {}", e.getMessage());
                    }
                })
                .toList();
        return new PageImpl<>(cartDtos, pageable, cartDtos.size());
    }

    @Override
    public CartDto findById(Integer cartId) {
        log.info("CartDto, service; fetch cart by id");
        CartDto cartDto = cartRepository.findById(cartId)
                .map(CartMappingHelper::map)
                .orElseThrow(() -> new CartNotFoundException(String.format("Cart with id: %d not found", cartId)));
        try {
            cartDto.setUserDto(callAPI.receiverUserDto(cartDto.getUserDto().getId(), JwtTokenFilter.getTokenFromRequest()));
        } catch (Exception e) {
            log.error("Error fetching user info: {}", e.getMessage());
        }
        return cartDto;
    }

    @Override
    public CartDto save(CartDto cartDto) {
        log.info("CartDto, service; save cart");
        return modelMapper.map(cartRepository.save(modelMapper.map(cartDto, Cart.class)), CartDto.class);
    }

    @Override
    public CartDto update(CartDto cartDto) {
        log.info("CartDto, service; update cart");
        return CartMappingHelper.map(cartRepository.save(CartMappingHelper.map(cartDto)));
    }

    @Override
    public CartDto update(Integer cartId, CartDto cartDto) {
        log.info("CartDto, service; update cart with cartId");
        CartDto existingCartDto = findById(cartId);
        modelMapper.map(cartDto, existingCartDto);
        return CartMappingHelper.map(cartRepository.save(CartMappingHelper.map(existingCartDto)));
    }

    @Override
    public void deleteById(Integer cartId) {
        log.info("Void, service; delete cart by id");
        cartRepository.findById(cartId)
                .ifPresent(cart -> {
                    orderRepository.deleteAllByCart(cart);
                    cartRepository.deleteById(cartId);
                });
    }
}
