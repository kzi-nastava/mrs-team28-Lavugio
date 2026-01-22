package com.backend.lavugio.model.ride;

import com.backend.lavugio.dto.ride.RideReviewDTO;
import com.backend.lavugio.model.user.RegularUser;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int carRating;
    @Column(nullable = false)
    private int driverRating;

    @Column(nullable = false)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "ride_id")
    private Ride reviewedRide;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private RegularUser reviewer;

    public Review(Ride ride, RegularUser reviewer, RideReviewDTO rideReviewDTO) {
        this.carRating = rideReviewDTO.getVehicleRating();
        this.driverRating = rideReviewDTO.getDriverRating();
        this.comment = rideReviewDTO.getComment();
        this.reviewedRide = ride;
        this.reviewer = reviewer;
    }
}
