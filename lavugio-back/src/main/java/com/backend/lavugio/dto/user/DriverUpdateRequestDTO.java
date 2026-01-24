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
    private String vehicleMake;
    @Column(nullable = false)
    private String vehicleModel;
    @Column(nullable = false)
    private String vehicleLicensePlate;
    @Column(nullable = false)
    private int vehicleSeats;
    @Column(nullable = false)
    private boolean vehiclePetFriendly;
    @Column(nullable = false)
    private boolean vehicleBabyFriendly;
    @Column(nullable = false)
    private String vehicleColor;

    @Enumerated(EnumType.STRING)
    @Column
    private VehicleType vehicleType;
}
