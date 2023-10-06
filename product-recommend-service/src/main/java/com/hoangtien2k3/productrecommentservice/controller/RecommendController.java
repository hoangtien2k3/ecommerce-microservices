package com.hoangtien2k3.productrecommentservice.controller;

import com.hoangtien2k3.productrecommentservice.feignclient.ProductClient;
import com.hoangtien2k3.productrecommentservice.feignclient.UserClient;
import com.hoangtien2k3.productrecommentservice.http.HeaderGenerator;
import com.hoangtien2k3.productrecommentservice.model.Product;
import com.hoangtien2k3.productrecommentservice.model.Recommend;
import com.hoangtien2k3.productrecommentservice.model.User;
import com.hoangtien2k3.productrecommentservice.service.RecommendService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    private final RecommendService recommendService;
    private final ProductClient productClient;
    private final UserClient userClient;
    private final HeaderGenerator headerGenerator;

    @Autowired
    public RecommendController(RecommendService recommendService,
                               ProductClient productClient,
                               UserClient userClient,
                               HeaderGenerator headerGenerator) {
        this.recommendService = recommendService;
        this.productClient = productClient;
        this.userClient = userClient;
        this.headerGenerator = headerGenerator;
    }

    @GetMapping(value = "/recommends")
    private ResponseEntity<List<Recommend>> getAllRating(@RequestParam("name") String productName) {
        List<Recommend> recommendations = recommendService.getAllRecommendationByProductName(productName);
        if (!recommendations.isEmpty()) {
            return new ResponseEntity<List<Recommend>>(
                    recommendations,
                    headerGenerator.getHeadersForSuccessGetMethod(),
                    HttpStatus.OK);
        }
        return new ResponseEntity<List<Recommend>>(
                headerGenerator.getHeadersForError(),
                HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/{userId}/recommends/{productId}")
    private ResponseEntity<Recommend> saveRecommendations(@PathVariable("userId") Long userId,
                                                          @PathVariable("productId") Long productId,
                                                          @RequestParam("rating") int rating,
                                                          HttpServletRequest request) {
        Product product = productClient.getProductById(productId);
        User user = userClient.getUserById(userId);

        if (product != null && user != null) {
            try {
                Recommend recommendation = new Recommend();
                recommendation.setProduct(product);
                recommendation.setUser(user);
                recommendation.setRating(rating);
                recommendService.saveRecommendation(recommendation);
                return new ResponseEntity<Recommend>(
                        recommendation,
                        headerGenerator.getHeadersForSuccessPostMethod(request, recommendation.getId()),
                        HttpStatus.CREATED);
            } catch (Exception e) {
                log.error("Error is " + e);
                return new ResponseEntity<Recommend>(
                        headerGenerator.getHeadersForError(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<Recommend>(
                headerGenerator.getHeadersForError(),
                HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/recommends/{id}")
    private ResponseEntity<Void> deleteRecommendations(@PathVariable("id") Long id) {
        Recommend recommendation = recommendService.getRecommendationById(id);
        if (recommendation != null) {
            try {
                recommendService.deleteRecommendation(id);
                return new ResponseEntity<Void>(
                        headerGenerator.getHeadersForSuccessGetMethod(),
                        HttpStatus.OK);
            } catch (Exception e) {
                log.error("Error is " + e);
                return new ResponseEntity<Void>(
                        headerGenerator.getHeadersForError(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<Void>(
                headerGenerator.getHeadersForError(),
                HttpStatus.NOT_FOUND);
    }

}
