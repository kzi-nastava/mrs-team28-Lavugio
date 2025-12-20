package com.backend.lavugio.model.user;

import com.backend.lavugio.model.vehicle.Vehicle;
import jakarta.persistence.*;

@Entity
@Table(name = "drivers")
public class Driver extends BlockableAccount {
	@Column
	private boolean isActive;

	@OneToOne()
	@JoinColumn(name = "vehicle_id")
	private Vehicle vehicle;
}
