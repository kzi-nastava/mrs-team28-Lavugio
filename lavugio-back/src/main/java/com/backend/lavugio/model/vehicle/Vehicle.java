package com.backend.lavugio.model.vehicle;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String make;
    @Column(nullable = false)
    private String model;
    @Column(nullable = false)
    private String licensePlate;
    @Column(nullable = false)
    private int seatsNumber;
    @Column(nullable = false)
    private boolean petFriendly;
    @Column(nullable = false)
    private boolean babyFriendly;
    @Column(nullable = false)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column
    private VehicleType type;
}
