package com.example.lavugio_mobile.models;

import java.time.LocalDateTime;
import java.util.List;

public class RideMonitoringModel {
    private long rideId;
    private long driverId;
    private String driverName;
    private LocalDateTime startTime;
    private String startAddress;
    private String endAddress;
    private List<Coordinates> checkpoints;
    private boolean panicked;

    public long getRideId() { return rideId; }
    public void setRideId(long rideId) { this.rideId = rideId; }

    public long getDriverId() { return driverId; }
    public void setDriverId(long driverId) { this.driverId = driverId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public String getStartAddress() { return startAddress; }
    public void setStartAddress(String startAddress) { this.startAddress = startAddress; }

    public String getEndAddress() { return endAddress; }
    public void setEndAddress(String endAddress) { this.endAddress = endAddress; }

    public List<Coordinates> getCheckpoints() { return checkpoints; }
    public void setCheckpoints(List<Coordinates> checkpoints) { this.checkpoints = checkpoints; }

    public boolean isPanicked() { return panicked; }
    public void setPanicked(boolean panicked) { this.panicked = panicked; }
}