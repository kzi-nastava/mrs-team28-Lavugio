package com.backend.lavugio.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private String email;
    private String name;
    private String surname;
    private String phoneNumber;
    private String address;
    private String profilePhotoPath;
    private String role; // "DRIVER", "PASSENGER", "ADMIN"

    // Driver info
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleColor;
    private String vehicleLicensePlate;
    private Integer vehicleSeats;
    private Boolean vehiclePetFriendly;
    private Boolean vehicleBabyFriendly;
    private String vehicleType;
    private String activeTime;
}
