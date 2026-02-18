package com.backend.lavugio.dto.ride;

import com.backend.lavugio.model.ride.Review;
import com.google.firebase.database.annotations.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetRideReviewDTO {
    @NotNull
    private Long reviewId;

    @Max(value = 5, message = "Rating can not exceed 5")
    @Min(value = 1, message = "Rating can not exceed 1")
    private int vehicleRating;

    @Max(value = 5, message = "Rating can not exceed 5")
    @Min(value = 1, message = "Rating can not exceed 1")
    private int driverRating;

    private String comment;

    public GetRideReviewDTO(Review review) {
        this.reviewId = review.getId();
        this.vehicleRating = review.getCarRating();
        this.driverRating = review.getDriverRating();
        this.comment = review.getComment();
    }
}
