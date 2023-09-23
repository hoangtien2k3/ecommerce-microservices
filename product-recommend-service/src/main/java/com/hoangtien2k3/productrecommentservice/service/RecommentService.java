package com.hoangtien2k3.productrecommentservice.service;

import com.hoangtien2k3.productrecommentservice.model.Recommend;

import java.util.List;

public interface RecommentService {
    Recommend getRecommendationById(Long recommendationId);
    Recommend saveRecommendation(Recommend recommendation);
    List<Recommend> getAllRecommendationByProductName(String productName);
    void deleteRecommendation(Long id);
}
