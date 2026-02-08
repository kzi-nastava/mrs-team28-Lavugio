package com.example.lavugio_mobile.data.model.user;

public class DriverUpdateRequest {
    private String address;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleLicensePlate;
    private int vehicleSeats;
    private boolean vehiclePetFriendly;
    private boolean vehicleBabyFriendly;
    private String vehicleColor;
    private String vehicleType;

    public DriverUpdateRequest(String address, String vehicleMake, String vehicleModel,
                               String vehicleLicensePlate, int vehicleSeats,
                               boolean vehiclePetFriendly, boolean vehicleBabyFriendly,
                               String vehicleColor, String vehicleType) {
        this.address = address;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
        this.vehicleLicensePlate = vehicleLicensePlate;
        this.vehicleSeats = vehicleSeats;
        this.vehiclePetFriendly = vehiclePetFriendly;
        this.vehicleBabyFriendly = vehicleBabyFriendly;
        this.vehicleColor = vehicleColor;
        this.vehicleType = vehicleType;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getVehicleMake() { return vehicleMake; }
    public void setVehicleMake(String vehicleMake) { this.vehicleMake = vehicleMake; }
    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
    public String getVehicleLicensePlate() { return vehicleLicensePlate; }
    public void setVehicleLicensePlate(String vehicleLicensePlate) { this.vehicleLicensePlate = vehicleLicensePlate; }
    public int getVehicleSeats() { return vehicleSeats; }
    public void setVehicleSeats(int vehicleSeats) { this.vehicleSeats = vehicleSeats; }
    public boolean isVehiclePetFriendly() { return vehiclePetFriendly; }
    public void setVehiclePetFriendly(boolean vehiclePetFriendly) { this.vehiclePetFriendly = vehiclePetFriendly; }
    public boolean isVehicleBabyFriendly() { return vehicleBabyFriendly; }
    public void setVehicleBabyFriendly(boolean vehicleBabyFriendly) { this.vehicleBabyFriendly = vehicleBabyFriendly; }
    public String getVehicleColor() { return vehicleColor; }
    public void setVehicleColor(String vehicleColor) { this.vehicleColor = vehicleColor; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
}
