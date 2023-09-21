package com.hoangtien2k3.orderserivce.service.impl;

import com.hoangtien2k3.orderserivce.entity.Item;
import com.hoangtien2k3.orderserivce.entity.Product;
import com.hoangtien2k3.orderserivce.feignclient.ProductClient;
import com.hoangtien2k3.orderserivce.redis.CartRedisRepository;
import com.hoangtien2k3.orderserivce.service.CartService;
import com.hoangtien2k3.orderserivce.utility.CartUtilities;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartSercviceImpl implements CartService {

    @Autowired
    private ProductClient productClient;

    @Autowired
    private CartRedisRepository cartRedisRepository;

    @Override
    public void addItemToCart(String cartId, Long productId, Integer quantity) {
        Product product = productClient.getProductById(productId);
        Item item = new Item(quantity, product, CartUtilities.getSubTotalForItem(product, quantity));
        cartRedisRepository.addItemToCart(cartId, item);
    }

    @Override
    public List<Object> getCart(String cartId) {
        return (List<Object>) cartRedisRepository.getCart(cartId, Item.class);
    }

    @Override
    public void changeItemQuantitty(String cartId, Long productId, Integer quantity) {
        List<Item> cart = (List) cartRedisRepository.getCart(cartId, Item.class);
        for (Item item : cart) {
            if ((item.getProduct().getId()).equals(productId)) {
                cartRedisRepository.deleteItemFromCart(cartId, item);
                item.setQuantity(quantity);
                item.setSubtotal(CartUtilities.getSubTotalForItem(item.getProduct(), quantity));
                cartRedisRepository.addItemToCart(cartId, item);
            }
        }
    }

    @Override
    public void deleteItemForCart(String cartId, Long productId) {
        List<Item> cart = (List) cartRedisRepository.getCart(cartId, Item.class);
        for (Item item : cart) {
            if ((item.getProduct().getId()).equals(productId)) {
                cartRedisRepository.deleteItemFromCart(cartId, item);
            }
        }
    }

    @Override
    public boolean checkIfItemIsExist(String cartId, Long productId) {
        List<Item> cart = (List) cartRedisRepository.getCart(cartId, Item.class);
        for (Item item : cart) {
            if ((item.getProduct().getId()).equals(productId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Item> getAllItemsFromCart(String cartId) {
        List<Item> items = (List) cartRedisRepository.getCart(cartId, Item.class);
        return items;
    }

    @Override
    public void deleteCart(String cartId) {
        cartRedisRepository.deleteCart(cartId);
    }
}
