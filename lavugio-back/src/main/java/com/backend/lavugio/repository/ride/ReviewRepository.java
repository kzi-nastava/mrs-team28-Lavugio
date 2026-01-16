package com.backend.lavugio.repository.ride;

import com.backend.lavugio.model.ride.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Find reviews by ride (reviewedRid field)
    List<Review> findByReviewedRide(com.backend.lavugio.model.ride.Ride ride);
    List<Review> findByReviewedRideId(Long rideId);

    // Find reviews by user who wrote them
    List<Review> findByReviewer(com.backend.lavugio.model.user.RegularUser user);
    List<Review> findByReviewerId(Long userId);

    // Find by ratings
    List<Review> findByDriverRating(int rating);
    List<Review> findByCarRating(int rating);
    List<Review> findByDriverRatingGreaterThanEqual(int minRating);
    List<Review> findByCarRatingGreaterThanEqual(int minRating);

    // Check if review exists for a ride by a user
    boolean existsByReviewedRideIdAndReviewerId(Long rideId, Long userId);

    // Get average ratings for a driver (through rides)
    @Query("SELECT AVG(r.driverRating) FROM Review r " +
            "WHERE r.reviewedRide.driver.id = :driverId")
    Optional<Double> getAverageDriverRating(@Param("driverId") Long driverId);

    @Query("SELECT AVG(r.carRating) FROM Review r " +
            "WHERE r.reviewedRide.driver.id = :driverId")
    Optional<Double> getAverageCarRating(@Param("driverId") Long driverId);

    // Get all reviews for a driver (through all their rides)
    @Query("SELECT r FROM Review r WHERE r.reviewedRide.driver.id = :driverId " +
            "ORDER BY r.id DESC")
    List<Review> findAllReviewsForDriver(@Param("driverId") Long driverId);

    // Count reviews for a driver
    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewedRide.driver.id = :driverId")
    long countReviewsForDriver(@Param("driverId") Long driverId);

    // Find low-rated reviews (for moderation)
    List<Review> findByDriverRatingLessThan(int maxRating);
    List<Review> findByCarRatingLessThan(int maxRating);
}