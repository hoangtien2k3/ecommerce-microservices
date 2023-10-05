package com.hoangtien2k3.productcatalogservice.dto;

import com.hoangtien2k3.productcatalogservice.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PageResult {
    private List<Product> products;
    private int totalProducts;

    public PageResult(List<Product> products, int totalProducts) {
        this.products = products;
        this.totalProducts = totalProducts;
    }
}
