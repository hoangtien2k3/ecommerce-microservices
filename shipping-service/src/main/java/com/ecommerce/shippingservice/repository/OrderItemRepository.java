package com.ecommerce.shippingservice.repository;

import com.ecommerce.shippingservice.domain.OrderItem;
import com.ecommerce.shippingservice.domain.id.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {

}
