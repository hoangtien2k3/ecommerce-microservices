package com.ecommerce.rating.viewmodel;

import java.util.List;

public record RatingListVm(
        List<RatingVm> ratingList,
        long totalElements,
        int totalPages
) {

}
