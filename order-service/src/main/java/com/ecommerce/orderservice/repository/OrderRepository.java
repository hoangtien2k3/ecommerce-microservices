package com.ecommerce.orderservice.repository;

import com.ecommerce.orderservice.entity.Cart;
import com.ecommerce.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Modifying
    @Query("DELETE FROM Order o WHERE o.cart = :cart")
    void deleteAllByCart(Cart cart);
}
