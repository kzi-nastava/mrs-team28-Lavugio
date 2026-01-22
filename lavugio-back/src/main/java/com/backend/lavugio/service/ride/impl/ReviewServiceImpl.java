package com.backend.lavugio.service.ride.impl;

import com.backend.lavugio.dto.ride.GetRideReviewDTO;
import com.backend.lavugio.dto.ride.RideReviewDTO;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.ride.Review;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.repository.ride.ReviewRepository;
import com.backend.lavugio.service.ride.ReviewService;
import com.backend.lavugio.service.ride.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private final ReviewRepository reviewRepository;
    @Autowired
    private final RideService rideService;

    @Override
    @Transactional
    public Review createReview(Long rideId, RideReviewDTO rideReviewDTO) {
        Ride ride = rideService.getRideById(rideId);
        if (ride == null) {
            throw new IllegalArgumentException("Ride cannot be null");
        }
        if (ride.getCreator() == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (ride.getRideStatus() != RideStatus.FINISHED) {
            throw  new IllegalArgumentException("Ride is not finished");
        }
        if (ride.getEndDateTime().plusHours(24*3).isAfter(LocalDateTime.now())){
            throw new IllegalArgumentException("Ride cannot be reviewed 3 days after concluding");
        }

        Review review = new Review(ride, rideReviewDTO);
        if (reviewRepository.existsByReviewedRideIdAndReviewerId(
                review.getReviewedRide().getId(),
                review.getReviewedRide().getCreator().getId())) {
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
    public List<GetRideReviewDTO> getRideReviewDTOsByRideId(Long rideId){
        List<Review> reviews =  this.getReviewsByRideId(rideId);
        List<GetRideReviewDTO> dtos = new ArrayList<>();
        for (Review review : reviews) {
            GetRideReviewDTO dto = new GetRideReviewDTO(review);
        }
        return dtos;
    }
}