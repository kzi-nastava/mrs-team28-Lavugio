package com.backend.lavugio.dto.ride;

import com.backend.lavugio.model.ride.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetRideReviewDTO {
    private Long reviewId;
    private int vehicleRating;
    private int driverRating;
    private String comment;

    public GetRideReviewDTO(Review review) {
        this.reviewId = review.getId();
        this.vehicleRating = review.getCarRating();
        this.driverRating = review.getDriverRating();
        this.comment = review.getComment();
    }
}
