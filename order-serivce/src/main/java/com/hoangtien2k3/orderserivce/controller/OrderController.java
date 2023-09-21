package com.hoangtien2k3.orderserivce.controller;

import com.hoangtien2k3.orderserivce.entity.Item;
import com.hoangtien2k3.orderserivce.entity.Order;
import com.hoangtien2k3.orderserivce.entity.User;
import com.hoangtien2k3.orderserivce.feignclient.UserClient;
import com.hoangtien2k3.orderserivce.service.CartService;
import com.hoangtien2k3.orderserivce.service.OrderService;
import com.hoangtien2k3.orderserivce.service.http.HeaderGenerator;
import com.hoangtien2k3.orderserivce.utility.OrderUtilites;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private UserClient userClient;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private HeaderGenerator headerGenerator;

    @PostMapping(value = "/order/{userId}")
    public ResponseEntity<Order> saveOrder(
            @PathVariable("userId") Long userId,
            @RequestHeader(value = "Cookie") String cartId,
            HttpServletRequest request){

        List<Item> cart = cartService.getAllItemsFromCart(cartId);
        User user = userClient.getUserById(userId);
        if(cart != null && user != null) {
            Order order = this.createOrder(cart, user);
            try{
                orderService.saveOrder(order);
                cartService.deleteCart(cartId);
                return new ResponseEntity<Order>(
                        order,
                        headerGenerator.getHeadersSuccessPostMethod(request, order.getId()),
                        HttpStatus.CREATED);
            }catch (Exception ex){
                ex.printStackTrace();
                return new ResponseEntity<Order>(
                        headerGenerator.getHeadersError(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Order>(
                headerGenerator.getHeadersError(),
                HttpStatus.NOT_FOUND);
    }

    private Order createOrder(List<Item> cart, User user) {
        return Order.builder()
                .items(cart)
                .user(user)
                .total(OrderUtilites.countTotalPrice(cart))
                .orderedDate(LocalDate.now())
                .status("PAYMENT_EXPECTED")
                .build();
    }

}
