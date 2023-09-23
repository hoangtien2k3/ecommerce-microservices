package com.hoangtien2k3.orderservice.repository;

import com.hoangtien2k3.orderservice.model.OrderLineItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLineItemsRepository extends JpaRepository<OrderLineItems, Long> {
}
