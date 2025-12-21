package com.backend.lavugio.service.ride;

import com.backend.lavugio.model.ride.Review;
import java.util.List;

public interface ReviewService {
    Review createReview(Review review);
    Review getReviewById(Long id);
    List<Review> getAllReviews();
    List<Review> getReviewsByRideId(Long rideId);
    List<Review> getReviewsByUserId(Long userId);
    List<Review> getReviewsForDriver(Long driverId);
    Double getAverageDriverRating(Long driverId);
    Double getAverageCarRating(Long driverId);
    Review updateReview(Long id, Review review);
    void deleteReview(Long id);
}