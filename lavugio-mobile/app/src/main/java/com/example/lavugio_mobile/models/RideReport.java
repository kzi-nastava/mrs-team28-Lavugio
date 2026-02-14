package com.example.lavugio_mobile.models;

public class RideReport {
    private long rideId;
    private String comment;
    private long reporterId;

    public RideReport() {}

    public RideReport(long rideId, String comment, long reporterId) {
        this.rideId = rideId;
        this.comment = comment;
        this.reporterId = reporterId;
    }

    public long getRideId() { return rideId; }
    public void setRideId(long rideId) { this.rideId = rideId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public long getReporterId() { return reporterId; }
    public void setReporterId(long reporterId) { this.reporterId = reporterId; }
}