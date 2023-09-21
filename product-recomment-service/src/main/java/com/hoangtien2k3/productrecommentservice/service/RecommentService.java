package com.hoangtien2k3.productrecommentservice.service;

import com.hoangtien2k3.productrecommentservice.model.Recomment;

import java.util.List;

public interface RecommentService {
    Recomment getRecommendationById(Long recommendationId);
    Recomment saveRecommendation(Recomment recommendation);
    List<Recomment> getAllRecommendationByProductName(String productName);
    void deleteRecommendation(Long id);
}
