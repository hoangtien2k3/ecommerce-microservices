package com.hoangtien2k3.orderserivce.controller;

import com.hoangtien2k3.orderserivce.service.CartService;
import com.hoangtien2k3.orderserivce.service.http.HeaderGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private HeaderGenerator headerGenerator;

    @GetMapping(value = "/cart")
    public ResponseEntity<List<Object>> getCart(@RequestHeader(value = "Cookie") String cartId) {
        List<Object> cart = cartService.getCart(cartId);
        if (!cart.isEmpty()) {
            return new ResponseEntity<List<Object>>(
                    cart,
                    headerGenerator.getHeadersSuccessGetMethod(),
                    HttpStatus.OK);
        }
        return new ResponseEntity<List<Object>>(
                headerGenerator.getHeadersError(),
                HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/cart", params = {"productId", "quantity"})
    public ResponseEntity<List<Object>> addItemToCart(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity,
            @RequestHeader(value = "Cookie") String cartId,
            HttpServletRequest request) {

        List<Object> cart = cartService.getCart(cartId);
        if (cart != null) {
            if (cart.isEmpty()) {
                cartService.addItemToCart(cartId, productId, quantity);
            } else {
                if (cartService.checkIfItemIsExist(cartId, productId)) {
                    cartService.changeItemQuantitty(cartId, productId, quantity);
                } else {
                    cartService.addItemToCart(cartId, productId, quantity);
                }
            }
            return new ResponseEntity<List<Object>>(
                    cart,
                    headerGenerator.getHeadersSuccessPostMethod(request, Long.parseLong(cartId)),
                    HttpStatus.CREATED);
        }

        return new ResponseEntity<List<Object>>(
                headerGenerator.getHeadersError(),
                HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/cart", params = "productId")
    public ResponseEntity<Void> removeItemFromCart(
            @RequestParam("productId") Long productId,
            @RequestHeader(value = "Cookie") String cartId) {
        List<Object> cart = cartService.getCart(cartId);
        if (cart != null) {
            cartService.deleteItemForCart(cartId, productId);
            return new ResponseEntity<Void>(
                    headerGenerator.getHeadersSuccessGetMethod(),
                    HttpStatus.OK);
        }
        return new ResponseEntity<Void>(
                headerGenerator.getHeadersError(),
                HttpStatus.NOT_FOUND);
    }

}
