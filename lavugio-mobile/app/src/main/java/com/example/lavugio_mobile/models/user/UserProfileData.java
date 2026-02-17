package com.example.lavugio_mobile.models.user;

public class UserProfileData {
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

    public UserProfileData(String email, String name, String surname, String phoneNumber, String address, String profilePhotoPath, String role, String vehicleMake, String vehicleModel, String vehicleColor, String vehicleLicensePlate, Integer vehicleSeats, Boolean vehiclePetFriendly, Boolean vehicleBabyFriendly, String vehicleType, String activeTime) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.profilePhotoPath = profilePhotoPath;
        this.role = role;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
        this.vehicleColor = vehicleColor;
        this.vehicleLicensePlate = vehicleLicensePlate;
        this.vehicleSeats = vehicleSeats;
        this.vehiclePetFriendly = vehiclePetFriendly;
        this.vehicleBabyFriendly = vehicleBabyFriendly;
        this.vehicleType = vehicleType;
        this.activeTime = activeTime;
    }

    public UserProfileData() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfilePhotoPath() {
        return profilePhotoPath;
    }

    public void setProfilePhotoPath(String profilePhotoPath) {
        this.profilePhotoPath = profilePhotoPath;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public Integer getVehicleSeats() {
        return vehicleSeats;
    }

    public void setVehicleSeats(Integer vehicleSeats) {
        this.vehicleSeats = vehicleSeats;
    }

    public Boolean getVehiclePetFriendly() {
        return vehiclePetFriendly;
    }

    public void setVehiclePetFriendly(Boolean vehiclePetFriendly) {
        this.vehiclePetFriendly = vehiclePetFriendly;
    }

    public Boolean getVehicleBabyFriendly() {
        return vehicleBabyFriendly;
    }

    public void setVehicleBabyFriendly(Boolean vehicleBabyFriendly) {
        this.vehicleBabyFriendly = vehicleBabyFriendly;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(String activeTime) {
        this.activeTime = activeTime;
    }
}
