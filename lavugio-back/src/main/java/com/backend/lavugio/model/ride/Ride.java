package com.backend.lavugio.model.ride;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.backend.lavugio.model.route.Address;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.RegularUser;
import jakarta.persistence.*;

@Entity
@Table(name = "rides")
public class Ride {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "driver_id")
	private Driver driver;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "ride_passangers",
			joinColumns = @JoinColumn(name = "ride_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	private Set<RegularUser> passangers = new HashSet<>();

	// PROVERITI KASNIJE DA LI JE POTREBNO I KAKO RADI
	//@Column(columnDefinition = "TEXT")
	//private String routeGeometry;

	@Column(nullable = false)
	private LocalDate date;

	@Column(nullable = false)
	private LocalTime timeStart;

	@Column(nullable = false)
	private LocalTime timeEnd;

	@Column(nullable = false)
	private float price;

	@Column(nullable = false)
	private float distance;

	@Column(nullable = false)
	private boolean isCancelled;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private RideStatus rideStatus;

}
