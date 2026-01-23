package com.backend.lavugio.model.user;

import com.backend.lavugio.model.enums.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "driver_update_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverUpdateRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long driverId;

    @Column
    private String name;
    @Column
    private String lastName;
    @Column
    private String phoneNumber;
    @Column
    private String address;

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

    @Column
    private boolean validated;
}
