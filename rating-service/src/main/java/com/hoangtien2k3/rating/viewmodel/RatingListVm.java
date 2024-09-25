package com.hoangtien2k3.rating.viewmodel;

import java.util.List;

public record RatingListVm(
        List<RatingVm> ratingList,
        long totalElements,
        int totalPages
) {

}
