package com.example.lavugio_mobile.models.user;

public class DriverRegistrationDTO {
    private String email;
    private String password;
    private String name;
    private String lastName;
    private String phoneNumber;
    private String address;

    private String vehicleMake;
    private String vehicleModel;
    private String licensePlate;
    private String vehicleColor;
    private String vehicleType;

    private int passangerSeats;

    private boolean petFriendly;
    private boolean babyFriendly;

    public DriverRegistrationDTO() {}

    public DriverRegistrationDTO(String email, String password, String name, String lastName, String phoneNumber, String address,
                              String vehicleMake, String vehicleModel, String licensePlate, String vehicleColor, String vehicleType,
                              int passangerSeats, boolean petFriendly, boolean babyFriendly) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
        this.licensePlate = licensePlate;
        this.vehicleColor = vehicleColor;
        this.vehicleType = vehicleType;
        this.passangerSeats = passangerSeats;
        this.petFriendly = petFriendly;
        this.babyFriendly = babyFriendly;
    }

    // Getters and setters for all fields

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getVehicleMake() { return vehicleMake; }
    public void setVehicleMake(String vehicleMake) { this.vehicleMake = vehicleMake; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getVehicleColor() { return vehicleColor; }
    public void setVehicleColor(String vehicleColor) { this.vehicleColor = vehicleColor; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public int getPassangerSeats() { return passangerSeats; }
    public void setPassangerSeats(int passangerSeats) { this.passangerSeats = passangerSeats; }

    public boolean isPetFriendly() { return petFriendly; }
    public void setPetFriendly(boolean petFriendly) { this.petFriendly = petFriendly; }

    public boolean isBabyFriendly() { return babyFriendly; }
    public void setBabyFriendly(boolean babyFriendly) { this.babyFriendly = babyFriendly; }
}