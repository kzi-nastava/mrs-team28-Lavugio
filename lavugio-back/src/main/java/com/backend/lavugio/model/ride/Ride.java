package com.backend.lavugio.model.ride;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.backend.lavugio.model.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.RegularUser;
import jakarta.persistence.*;

@Entity
@Table(name = "rides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ride {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "driver_id")
	private Driver driver;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "ride_passengers",
			joinColumns = @JoinColumn(name = "ride_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	private Set<RegularUser> passengers = new HashSet<>();

	// PROVERITI KASNIJE DA LI JE POTREBNO I KAKO RADI
	//@Column(columnDefinition = "TEXT")
	//private String routeGeometry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private RegularUser creator;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column
    private LocalDateTime endDateTime;

	@Column(nullable = false)
	private int estimatedDurationSeconds;

	@Column(nullable = false)
	private float price;

	@Column(nullable = false)
	private float distance;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private RideStatus rideStatus;

    @Column(nullable = false)
    private boolean hasPanic;
}
