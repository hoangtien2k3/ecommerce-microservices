package com.hoangtien2k3.search.viewmodel;

import com.hoangtien2k3.search.document.Product;

public record ProductNameGetVm(String name) {
    public static ProductNameGetVm fromModel(Product product) {
        return new ProductNameGetVm(
                product.getName()
        );
    }
}
