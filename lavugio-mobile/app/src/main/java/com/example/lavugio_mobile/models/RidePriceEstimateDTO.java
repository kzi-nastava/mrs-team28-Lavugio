package com.example.lavugio_mobile.models;

public class RidePriceEstimateDTO {
    private String selectedVehicleType;
    private float distanceMeters;

    public RidePriceEstimateDTO() {}

    public RidePriceEstimateDTO(String selectedVehicleType, float distanceMeters) {
        this.selectedVehicleType = selectedVehicleType;
        this.distanceMeters = distanceMeters;
    }

    public String getSelectedVehicleType() { return selectedVehicleType; }
    public void setSelectedVehicleType(String selectedVehicleType) { this.selectedVehicleType = selectedVehicleType; }

    public float getDistanceMeters() { return distanceMeters; }
    public void setDistanceMeters(float distanceMeters) { this.distanceMeters = distanceMeters; }
}
