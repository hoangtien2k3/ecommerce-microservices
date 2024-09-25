package com.hoangtien2k3.tax.viewmodel.taxclass;

import java.util.List;

public record TaxClassListGetVm(
    List<TaxClassVm> taxClassContent,
    int pageNo,
    int pageSize,
    int totalElements,
    int totalPages,
    boolean isLast
) {

}
