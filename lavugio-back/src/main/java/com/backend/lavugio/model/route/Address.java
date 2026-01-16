package com.backend.lavugio.model.route;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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

    @Override
    public String toString(){
        return streetName + " "+ streetNumber + " " + city + " " + country + " " + zipCode;
    }
}
