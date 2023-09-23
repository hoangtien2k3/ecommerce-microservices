package com.hoangtien2k3.productrecommentservice.service.impl;

import com.hoangtien2k3.productrecommentservice.model.Recommend;
import com.hoangtien2k3.productrecommentservice.repository.RecommentRepository;
import com.hoangtien2k3.productrecommentservice.service.RecommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommentServiceImpl implements RecommentService {

    @Autowired
    private RecommentRepository recommentRepository;

    @Override
    public Recommend saveRecommendation(Recommend recommend) {
        return recommentRepository.save(recommend);
    }

    @Override
    public List<Recommend> getAllRecommendationByProductName(String productName) {
        return recommentRepository.findAllRatingByProductName(productName);
    }

    @Override
    public void deleteRecommendation(Long id) {
        recommentRepository.deleteById(id);
    }

    @Override
    public Recommend getRecommendationById(Long recommendationId) {
        return recommentRepository.getOne(recommendationId);
    }

}
