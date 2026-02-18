package com.example.lavugio_mobile.models;

public class AdminHistoryModel {
    private Long rideId;
    private String startAddress;
    private String endAddress;
    private String startDate;
    private String endDate;
    private double price;
    private boolean cancelled;
    private String cancelledBy;
    private boolean panic;

    public AdminHistoryModel() {
    }

    public AdminHistoryModel(Long rideId, String startAddress, String endAddress, String startDate,
                             String endDate, double price, boolean cancelled, String cancelledBy, boolean panic) {
        this.rideId = rideId;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.cancelled = cancelled;
        this.cancelledBy = cancelledBy;
        this.panic = panic;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public boolean isPanic() {
        return panic;
    }

    public void setPanic(boolean panic) {
        this.panic = panic;
    }
}
