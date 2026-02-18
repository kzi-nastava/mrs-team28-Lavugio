package com.example.lavugio_mobile.models.user;

public class DriverEditProfileRequestDTO {
    private EditProfileDTO profile;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleColor;
    private String vehicleLicensePlate;
    private int vehicleSeats;
    private boolean vehiclePetFriendly;
    private boolean vehicleBabyFriendly;
    private String vehicleType;

    public DriverEditProfileRequestDTO() {
    }

    public DriverEditProfileRequestDTO(EditProfileDTO profile, String vehicleMake, String vehicleModel, String vehicleColor, String vehicleLicensePlate, int vehicleSeats, boolean vehiclePetFriendly, boolean vehicleBabyFriendly, String vehicleType) {
        this.profile = profile;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
        this.vehicleColor = vehicleColor;
        this.vehicleLicensePlate = vehicleLicensePlate;
        this.vehicleSeats = vehicleSeats;
        this.vehiclePetFriendly = vehiclePetFriendly;
        this.vehicleBabyFriendly = vehicleBabyFriendly;
        this.vehicleType = vehicleType;
    }

    public EditProfileDTO getProfile() {
        return profile;
    }

    public void setProfile(EditProfileDTO profile) {
        this.profile = profile;
    }

    public String getVehicleMake() {
        return vehicleMake;
    }

    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public String getVehicleLicensePlate() {
        return vehicleLicensePlate;
    }

    public void setVehicleLicensePlate(String vehicleLicensePlate) {
        this.vehicleLicensePlate = vehicleLicensePlate;
    }

    public int getVehicleSeats() {
        return vehicleSeats;
    }

    public void setVehicleSeats(int vehicleSeats) {
        this.vehicleSeats = vehicleSeats;
    }

    public boolean isVehiclePetFriendly() {
        return vehiclePetFriendly;
    }

    public void setVehiclePetFriendly(boolean vehiclePetFriendly) {
        this.vehiclePetFriendly = vehiclePetFriendly;
    }

    public boolean isVehicleBabyFriendly() {
        return vehicleBabyFriendly;
    }

    public void setVehicleBabyFriendly(boolean vehicleBabyFriendly) {
        this.vehicleBabyFriendly = vehicleBabyFriendly;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
}
