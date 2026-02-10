package com.example.lavugio_mobile.data.model.ride;

import android.os.Parcelable;

import com.example.lavugio_mobile.data.model.vehicle.VehicleType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RidePreferences {
    private boolean isPetFriendly;
    private boolean isBabyFriendly;
    private List<String> passengerEmails;
    private VehicleType vehicleType;

    public RidePreferences(boolean isPetFriendly, boolean isBabyFriendly, List<String> passengerEmails, VehicleType vehicleType) {
        this.isPetFriendly = isPetFriendly;
        this.isBabyFriendly = isBabyFriendly;
        this.passengerEmails = passengerEmails;
        this.vehicleType = vehicleType;
    }

    public RidePreferences() {
        passengerEmails = new ArrayList<>();
    }

    public boolean isPetFriendly() {
        return isPetFriendly;
    }

    public void setPetFriendly(boolean petFriendly) {
        isPetFriendly = petFriendly;
    }

    public boolean isBabyFriendly() {
        return isBabyFriendly;
    }

    public void setBabyFriendly(boolean babyFriendly) {
        isBabyFriendly = babyFriendly;
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
}
