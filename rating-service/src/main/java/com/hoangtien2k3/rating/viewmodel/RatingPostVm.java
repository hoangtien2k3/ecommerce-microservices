package com.hoangtien2k3.rating.viewmodel;

import lombok.Builder;

@Builder
public record RatingPostVm(
        String content, int star, Long productId, String productName
) {

}

