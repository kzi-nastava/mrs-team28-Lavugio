package com.backend.lavugio.service.ride;

import com.backend.lavugio.dto.ride.GetRideReviewDTO;
import com.backend.lavugio.dto.ride.RideReviewDTO;
import com.backend.lavugio.model.ride.Review;
import java.util.List;

public interface ReviewService {
    Review createReview(Long rideId, RideReviewDTO review);
    Review getReviewById(Long id);
    List<Review> getAllReviews();
    List<Review> getReviewsByRideId(Long rideId);
    List<Review> getReviewsByUserId(Long userId);
    List<Review> getReviewsForDriver(Long driverId);
    Double getAverageDriverRating(Long driverId);
    Double getAverageCarRating(Long driverId);
    Review updateReview(Long id, Review review);
    void deleteReview(Long id);
    List<GetRideReviewDTO> getRideReviewDTOsByRideId(Long rideId);
}