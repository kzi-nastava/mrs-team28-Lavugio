package com.backend.lavugio.dto.user;

import com.backend.lavugio.model.enums.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverUpdateRequestDTO {
    @Column
    private AccountUpdateDTO profile;

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
