package com.hoangtien2k3.orderserivce.utility;

import com.hoangtien2k3.orderserivce.entity.Item;

import java.math.BigDecimal;
import java.util.List;

public class OrderUtilites {
    public static BigDecimal countTotalPrice(List<Item> cart){
        BigDecimal total = BigDecimal.ZERO;
        for(int i = 0; i < cart.size(); i++){
            total = total.add(cart.get(i).getSubtotal());
        }
        return total;
    }
}
