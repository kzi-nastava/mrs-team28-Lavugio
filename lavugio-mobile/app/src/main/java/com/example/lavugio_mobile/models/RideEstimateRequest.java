package com.example.lavugio_mobile.models;

import com.example.lavugio_mobile.data.model.vehicle.Vehicle;
import com.example.lavugio_mobile.data.model.vehicle.VehicleType;

import java.util.List;

public class RideEstimateRequest {
    private List<Coordinates> checkpoints;
    private VehicleType vehicleType;

    public RideEstimateRequest() {}

    public RideEstimateRequest(List<Coordinates> checkpoints, VehicleType vehicleType) {
        this.checkpoints = checkpoints;
        this.vehicleType = vehicleType;
    }

    public List<Coordinates> getCheckpoints() { return checkpoints; }
    public void setCheckpoints(List<Coordinates> checkpoints) { this.checkpoints = checkpoints; }

    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
}