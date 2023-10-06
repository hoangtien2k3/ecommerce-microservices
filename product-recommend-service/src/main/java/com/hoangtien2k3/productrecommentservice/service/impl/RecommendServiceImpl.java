package com.hoangtien2k3.productrecommentservice.service.impl;

import com.hoangtien2k3.productrecommentservice.model.Recommend;
import com.hoangtien2k3.productrecommentservice.repository.RecommendRepository;
import com.hoangtien2k3.productrecommentservice.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private RecommendRepository recommendRepository;

    @Override
    public void saveRecommendation(Recommend recommend) {
        recommendRepository.save(recommend);
    }

    @Override
    public List<Recommend> getAllRecommendationByProductName(String productName) {
        return recommendRepository.findAllRatingByProductName(productName);
    }

    @Override
    public void deleteRecommendation(Long id) {
        recommendRepository.deleteById(id);
    }

    @Override
    public Recommend getRecommendationById(Long recommendationId) {
        return recommendRepository.getReferenceById(recommendationId);
    }

}
