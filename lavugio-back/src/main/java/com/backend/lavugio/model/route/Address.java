package com.backend.lavugio.model.route;

import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	private String streetName;
	@Column(nullable = false)
	private String city;
	@Column(nullable = false)
	private String country;
	@Column(nullable = false)
	private int streetNumber;
	@Column(nullable = false)
	private int zipCode;

	@Column(nullable = false)
	private Double longitude;
	@Column(nullable = false)
	private Double latitude;

}
