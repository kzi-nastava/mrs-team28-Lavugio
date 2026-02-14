package com.example.lavugio_mobile.models;

import com.example.lavugio_mobile.models.enums.RideStatus;

import java.time.LocalDateTime;
import java.util.List;

public class RideOverviewModel {
    private long rideId;
    private RideStatus status;
    private double price;
    private String driverName;
    private String startAddress;
    private String endAddress;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private List<Coordinates> checkpoints;
    private boolean reported;
    private boolean reviewed;
    private boolean hasPanic;

    public long getRideId() { return rideId; }
    public void setRideId(long rideId) { this.rideId = rideId; }

    public RideStatus getStatus() { return status; }
    public void setStatus(RideStatus status) { this.status = status; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getStartAddress() { return startAddress; }
    public void setStartAddress(String startAddress) { this.startAddress = startAddress; }

    public String getEndAddress() { return endAddress; }
    public void setEndAddress(String endAddress) { this.endAddress = endAddress; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public List<Coordinates> getCheckpoints() { return checkpoints; }
    public void setCheckpoints(List<Coordinates> checkpoints) { this.checkpoints = checkpoints; }

    public boolean isReported() { return reported; }
    public void setReported(boolean reported) { this.reported = reported; }

    public boolean isReviewed() { return reviewed; }
    public void setReviewed(boolean reviewed) { this.reviewed = reviewed; }

    public boolean isHasPanic() { return hasPanic; }
    public void setHasPanic(boolean hasPanic) { this.hasPanic = hasPanic; }
}