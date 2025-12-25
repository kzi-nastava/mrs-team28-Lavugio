package com.backend.lavugio.controller.ride;

import com.backend.lavugio.dto.GetRideReviewDTO;
import com.backend.lavugio.dto.RideReviewDTO;
import com.backend.lavugio.service.ride.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides/{rideId}/review")
public class RideReviewController {

    private final ReviewService reviewService;

    @Autowired
    public RideReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<?> reviewRide(@PathVariable Long rideId, RideReviewDTO rideReviewDTO){
        //reviewService.createReview(rideId, rideReviewDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetRideReviewDTO> getRideReviews(@PathVariable Long rideId){
        GetRideReviewDTO getRideReviewDTO = new GetRideReviewDTO(1L, 4, 5, "Great ride!");
        return new ResponseEntity<>(getRideReviewDTO, HttpStatus.OK);
    }
}
