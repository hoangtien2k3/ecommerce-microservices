package com.hoangtien2k3.orderserivce.utility;

import com.hoangtien2k3.orderserivce.entity.Product;

import java.math.BigDecimal;

public class CartUtilities {
    public static BigDecimal getSubTotalForItem(Product product, int quantity){
        return (product.getPrice()).multiply(BigDecimal.valueOf(quantity));
    }
}
