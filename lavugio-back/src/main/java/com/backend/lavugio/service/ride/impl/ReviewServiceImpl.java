package com.backend.lavugio.service.ride.impl;

import com.backend.lavugio.dto.ride.RideReviewDTO;
import com.backend.lavugio.model.ride.Review;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.repository.ride.ReviewRepository;
import com.backend.lavugio.service.ride.ReviewService;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.user.RegularUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final RideService rideService;
    private final RegularUserService  regularUserService;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, RideService rideService,  RegularUserService regularUserService) {
        this.reviewRepository = reviewRepository;
        this.rideService = rideService;
        this.regularUserService = regularUserService;
    }

    @Override
    @Transactional
    public Review createReview(Long rideId, Long userId, RideReviewDTO rideReviewDTO) {
        Ride ride = rideService.getRideById(rideId);
        RegularUser user =  regularUserService.getRegularUserById(userId);
        Review review = new Review(ride, user, rideReviewDTO);
        if (ride == null) {
            throw new IllegalArgumentException("Ride cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (hasReviewed(userId,  rideId)) {
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
        return reviewRepository.findByReviewedRideId(rideId);
    }

    @Override
    public List<Review> getReviewsByUserId(Long userId) {
        return reviewRepository.findByReviewerId(userId);
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

    @Override
    public boolean hasReviewed(Long userId, Long rideId) {
        List<Review> reviews =  getReviewsByRideId(rideId);
        if  (reviews.isEmpty()) {
            return false;
        }
        for  (Review review : reviews) {
            if (review.getReviewer().getId().equals(userId)) {
                return true;
            }
        }
        return false;
    }
}