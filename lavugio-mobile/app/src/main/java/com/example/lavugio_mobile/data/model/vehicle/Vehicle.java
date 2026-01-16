package com.example.lavugio_mobile.data.model.vehicle;

public class Vehicle {
    private Long id;
    private String make;
    private String model;
    private String color;
    private String licensePlate;
    private VehicleType type;
    private boolean petFriendly;
    private boolean babyFriendly;

    // Constructors
    public Vehicle() {}

    // Getters and Setters
    public Long getVehicleId() { return id; }
    public void setVehicleId(Long vehicleId) { this.id = vehicleId; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public VehicleType getType() { return type; }
    public void setType(VehicleType type) { this.type = type; }

    public boolean isPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public boolean isBabyFriendly() {
        return babyFriendly;
    }

    public void setBabyFriendly(boolean babyFriendly) {
        this.babyFriendly = babyFriendly;
    }

    public String getFullName() {
        return make + " " + model;
    }
}
