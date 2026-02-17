package com.example.lavugio_mobile.models;


import com.example.lavugio_mobile.data.model.vehicle.VehicleType;
import com.example.lavugio_mobile.models.ride.RideDestinationDTO;

import java.time.LocalDateTime;
import java.util.List;

public class RideRequestDTO {
    private List<RideDestinationDTO> destinations;
    private List<String> passengerEmails;
    private VehicleType vehicleType;
    private boolean babyFriendly;
    private boolean petFriendly;
    private LocalDateTime scheduledTime;
    private boolean scheduled;
    private int estimatedDurationSeconds;
    private int price;
    private float distance;

    public RideRequestDTO() {
    }

    public RideRequestDTO(List<RideDestinationDTO> destinations, List<String> passengerEmails, VehicleType vehicleType, boolean babyFriendly, boolean petFriendly, LocalDateTime scheduledTime, boolean scheduled, int estimatedDurationSeconds, int price, float distance) {
        this.destinations = destinations;
        this.passengerEmails = passengerEmails;
        this.vehicleType = vehicleType;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
        this.scheduledTime = scheduledTime;
        this.scheduled = scheduled;
        this.estimatedDurationSeconds = estimatedDurationSeconds;
        this.price = price;
        this.distance = distance;
    }

    public List<RideDestinationDTO> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<RideDestinationDTO> destinations) {
        this.destinations = destinations;
    }

    public List<String> getPassengerEmails() {
        return passengerEmails;
    }

    public void setPassengerEmails(List<String> passengerEmails) {
        this.passengerEmails = passengerEmails;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public boolean isBabyFriendly() {
        return babyFriendly;
    }

    public void setBabyFriendly(boolean babyFriendly) {
        this.babyFriendly = babyFriendly;
    }

    public boolean isPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    public int getEstimatedDurationSeconds() {
        return estimatedDurationSeconds;
    }

    public void setEstimatedDurationSeconds(int estimatedDurationSeconds) {
        this.estimatedDurationSeconds = estimatedDurationSeconds;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}