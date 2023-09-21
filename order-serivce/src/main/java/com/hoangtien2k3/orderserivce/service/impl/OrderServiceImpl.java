package com.hoangtien2k3.orderserivce.service.impl;

import com.hoangtien2k3.orderserivce.repository.OrderRepository;
import com.hoangtien2k3.orderserivce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hoangtien2k3.orderserivce.entity.Order;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

}
