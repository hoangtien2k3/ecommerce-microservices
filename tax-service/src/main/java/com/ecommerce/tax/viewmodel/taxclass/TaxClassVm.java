package com.ecommerce.tax.viewmodel.taxclass;

import com.ecommerce.tax.model.TaxClass;

public record TaxClassVm(Long id, String name) {

    public static TaxClassVm fromModel(TaxClass taxClass) {
        return new TaxClassVm(taxClass.getId(), taxClass.getName());
    }
}
