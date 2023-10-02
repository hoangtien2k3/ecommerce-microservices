package com.hoangtien2k3.shippingservice.repository;

import com.hoangtien2k3.shippingservice.domain.OrderItem;
import com.hoangtien2k3.shippingservice.domain.id.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {

}