package com.hoangtien2k3.tax.viewmodel.taxrate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record TaxRatePostVm(@NotNull Double rate,
                            @Size(max = 25) String zipCode,
                            @NotNull Long taxClassId,
                            Long stateOrProvinceId,
                            @NotNull Long countryId) {
}
