package com.example.lavugio_mobile.models;

import com.example.lavugio_mobile.models.enums.RideStatus;

import java.time.LocalDateTime;

public class RideOverviewUpdate {
    private RideStatus status;
    private String endAddress;
    private Double price;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Coordinates destinationCoordinates;

    public RideStatus getStatus() { return status; }
    public void setStatus(RideStatus status) { this.status = status; }

    public String getEndAddress() { return endAddress; }
    public void setEndAddress(String endAddress) { this.endAddress = endAddress; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public Coordinates getDestinationCoordinates() { return destinationCoordinates; }
    public void setDestinationCoordinates(Coordinates destinationCoordinates) { this.destinationCoordinates = destinationCoordinates; }
}