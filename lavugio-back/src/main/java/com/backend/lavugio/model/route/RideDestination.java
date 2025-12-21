package com.backend.lavugio.model.route;

import com.backend.lavugio.model.ride.Ride;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ride_destinations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideDestination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ride_id", nullable = false)
	private Ride ride;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(nullable = false)
    private Integer destinationOrder;
}
