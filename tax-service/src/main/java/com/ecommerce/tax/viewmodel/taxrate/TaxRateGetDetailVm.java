package com.ecommerce.tax.viewmodel.taxrate;

public record TaxRateGetDetailVm(Long id, Double rate, String zipCode, String taxClassName, String stateOrProvinceName,
                                 String countryName) {

}
