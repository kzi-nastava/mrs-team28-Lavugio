package com.backend.lavugio.controller;

import com.backend.lavugio.dto.ReviewDTO;
import com.backend.lavugio.service.ride.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rides/{rideId}/review")
public class RideReviewController {

    private final ReviewService reviewService;

    @Autowired
    public RideReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<?> reviewRide(@PathVariable Long rideId, ReviewDTO reviewDTO){
        reviewService.createReview(rideId, reviewDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
