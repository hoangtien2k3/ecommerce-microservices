package com.hoangtien2k3.orderservice.repository;

import com.hoangtien2k3.orderservice.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Integer> {

}