package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.order.CartDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CartService {
    List<CartDto> findAll();

    Page<CartDto> findAll(int page, int size, String sortBy, String sortOrder);

    CartDto findById(Integer cartId);

    CartDto save(CartDto cartDto);

    CartDto update(CartDto cartDto);

    CartDto update(Integer cartId, CartDto cartDto);

    void deleteById(Integer cartId);
}
