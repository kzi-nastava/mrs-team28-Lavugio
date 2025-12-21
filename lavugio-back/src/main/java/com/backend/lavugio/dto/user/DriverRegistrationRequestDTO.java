package com.backend.lavugio.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverRegistrationRequestDTO
{
    private String email;
    private String password;
    private String name;
    private String lastName;
    private String licensePlate;
    private String make;
    private String model;
    private int seatsNumber;
    private boolean petFriendly;
    private boolean babyFriendly;
    private String color;
    private String vehicleType;
    private String vehiclePlate;
}
