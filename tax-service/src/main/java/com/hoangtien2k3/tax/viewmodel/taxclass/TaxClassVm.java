package com.hoangtien2k3.tax.viewmodel.taxclass;

import com.hoangtien2k3.tax.model.TaxClass;

public record TaxClassVm(Long id, String name) {

    public static TaxClassVm fromModel(TaxClass taxClass) {
        return new TaxClassVm(taxClass.getId(), taxClass.getName());
    }
}
