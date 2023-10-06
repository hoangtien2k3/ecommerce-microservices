package com.hoangtien2k3.productrecommentservice.controller;

import com.hoangtien2k3.productrecommentservice.feignclient.ProductClient;
import com.hoangtien2k3.productrecommentservice.feignclient.UserClient;
import com.hoangtien2k3.productrecommentservice.http.HeaderGenerator;
import com.hoangtien2k3.productrecommentservice.model.Product;
import com.hoangtien2k3.productrecommentservice.model.Recommend;
import com.hoangtien2k3.productrecommentservice.model.User;
import com.hoangtien2k3.productrecommentservice.service.RecommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    @Autowired
    private RecommentService recommentService;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private HeaderGenerator headerGenerator;

    @GetMapping(value = "/recommends")
    private ResponseEntity<List<Recommend>> getAllRating(@RequestParam("name") String productName) {
        List<Recommend> recommendations = recommentService.getAllRecommendationByProductName(productName);
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
                recommentService.saveRecommendation(recommendation);
                return new ResponseEntity<Recommend>(
                        recommendation,
                        headerGenerator.getHeadersForSuccessPostMethod(request, recommendation.getId()),
                        HttpStatus.CREATED);
            } catch (Exception e) {
                e.printStackTrace();
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
        Recommend recommendation = recommentService.getRecommendationById(id);
        if (recommendation != null) {
            try {
                recommentService.deleteRecommendation(id);
                return new ResponseEntity<Void>(
                        headerGenerator.getHeadersForSuccessGetMethod(),
                        HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
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
