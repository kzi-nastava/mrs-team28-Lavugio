package com.backend.lavugio.model.vehicle;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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
