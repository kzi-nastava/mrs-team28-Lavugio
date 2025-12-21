package com.backend.lavugio.model.user;

import com.backend.lavugio.model.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Driver extends BlockableAccount {
	@Column
	private boolean active;

	@OneToOne()
	@JoinColumn(name = "vehicle_id")
	private Vehicle vehicle;
}
