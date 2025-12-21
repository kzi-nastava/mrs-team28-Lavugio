package com.backend.lavugio.service.ride.impl;

import com.backend.lavugio.model.ride.Review;
import com.backend.lavugio.repository.ride.ReviewRepository;
import com.backend.lavugio.service.ride.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public Review createReview(Review review) {
        if (review.getReviewedRid() == null) {
            throw new IllegalArgumentException("Ride cannot be null");
        }
        if (review.getReviewedByUser() == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (reviewRepository.existsByReviewedRidIdAndReviewedByUserId(
                review.getReviewedRid().getId(),
                review.getReviewedByUser().getId())) {
            throw new IllegalStateException("User has already reviewed this ride");
        }

        return reviewRepository.save(review);
    }

    @Override
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public List<Review> getReviewsByRideId(Long rideId) {
        return reviewRepository.findByReviewedRidId(rideId);
    }

    @Override
    public List<Review> getReviewsByUserId(Long userId) {
        return reviewRepository.findByReviewedByUserId(userId);
    }

    @Override
    public List<Review> getReviewsForDriver(Long driverId) {
        return reviewRepository.findAllReviewsForDriver(driverId);
    }

    @Override
    public Double getAverageDriverRating(Long driverId) {
        return reviewRepository.getAverageDriverRating(driverId).orElse(0.0);
    }

    @Override
    public Double getAverageCarRating(Long driverId) {
        return reviewRepository.getAverageCarRating(driverId).orElse(0.0);
    }

    @Override
    @Transactional
    public Review updateReview(Long id, Review updatedReview) {
        Review existingReview = getReviewById(id);

        existingReview.setCarRating(updatedReview.getCarRating());
        existingReview.setDriverRating(updatedReview.getDriverRating());
        existingReview.setComment(updatedReview.getComment());

        return reviewRepository.save(existingReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }
}