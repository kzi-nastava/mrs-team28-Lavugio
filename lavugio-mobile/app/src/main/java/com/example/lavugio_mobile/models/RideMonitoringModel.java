package com.example.lavugio_mobile.models;

import java.util.List;

public class RideMonitoringModel {
    private long rideId;
    private String status;
    private String driverName;
    private String passengerName;
    private String startAddress;
    private String endAddress;
    private List<Coordinates> checkpoints;

    public long getRideId() { return rideId; }
    public void setRideId(long rideId) { this.rideId = rideId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public String getStartAddress() { return startAddress; }
    public void setStartAddress(String startAddress) { this.startAddress = startAddress; }

    public String getEndAddress() { return endAddress; }
    public void setEndAddress(String endAddress) { this.endAddress = endAddress; }

    public List<Coordinates> getCheckpoints() { return checkpoints; }
    public void setCheckpoints(List<Coordinates> checkpoints) { this.checkpoints = checkpoints; }
}