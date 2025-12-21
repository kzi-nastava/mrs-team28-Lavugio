package com.backend.lavugio.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverProfileDTO extends AccountProfileDTO {
    private boolean active;
    private String make;
    private String model;
    private String licensePlate;
    private boolean petFriendly;
    private boolean babyFriendly;
    private String color;
    private String vehicleType;
}
