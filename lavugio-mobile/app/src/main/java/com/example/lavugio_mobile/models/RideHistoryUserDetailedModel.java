package com.example.lavugio_mobile.models;

import java.util.List;

/**
 * Detailed model for a single user ride history entry.
 * Contains all information needed for the detailed view including
 * driver info, reviews, reports, and route data.
 */
public class RideHistoryUserDetailedModel {
    private long rideId;
    private String start;
    private String end;
    private String departure;
    private String destination;
    private double price;
    private boolean cancelled;
    private boolean panic;

    // Driver info
    private Long driverId;
    private String driverName;
    private String driverLastName;
    private String driverPhotoPath;
    private String driverPhoneNumber;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleLicensePlate;
    private String vehicleColor;

    // Review info
    private Integer driverRating;
    private Integer carRating;
    private String reviewComment;
    private boolean hasReview;

    // Reports
    private List<ReportInfo> reports;

    // Checkpoints for map
    private List<Coordinates> checkpoints;

    // Full destination info for reordering
    private List<DestinationDetail> destinations;

    // Nested classes
    public static class ReportInfo {
        private long reportId;
        private String reportMessage;
        private String reporterName;

        public long getReportId() { return reportId; }
        public void setReportId(long reportId) { this.reportId = reportId; }

        public String getReportMessage() { return reportMessage; }
        public void setReportMessage(String reportMessage) { this.reportMessage = reportMessage; }

        public String getReporterName() { return reporterName; }
        public void setReporterName(String reporterName) { this.reporterName = reporterName; }
    }

    public static class DestinationDetail {
        private int orderIndex;
        private double latitude;
        private double longitude;
        private String address;
        private String streetName;
        private String city;
        private String country;
        private String streetNumber;
        private int zipCode;

        public int getOrderIndex() { return orderIndex; }
        public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }

        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getStreetName() { return streetName; }
        public void setStreetName(String streetName) { this.streetName = streetName; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getStreetNumber() { return streetNumber; }
        public void setStreetNumber(String streetNumber) { this.streetNumber = streetNumber; }

        public int getZipCode() { return zipCode; }
        public void setZipCode(int zipCode) { this.zipCode = zipCode; }
    }

    // Getters and setters
    public long getRideId() { return rideId; }
    public void setRideId(long rideId) { this.rideId = rideId; }

    public String getStart() { return start; }
    public void setStart(String start) { this.start = start; }

    public String getEnd() { return end; }
    public void setEnd(String end) { this.end = end; }

    public String getDeparture() { return departure; }
    public void setDeparture(String departure) { this.departure = departure; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    public boolean isPanic() { return panic; }
    public void setPanic(boolean panic) { this.panic = panic; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverLastName() { return driverLastName; }
    public void setDriverLastName(String driverLastName) { this.driverLastName = driverLastName; }

    public String getDriverPhotoPath() { return driverPhotoPath; }
    public void setDriverPhotoPath(String driverPhotoPath) { this.driverPhotoPath = driverPhotoPath; }

    public String getDriverPhoneNumber() { return driverPhoneNumber; }
    public void setDriverPhoneNumber(String driverPhoneNumber) { this.driverPhoneNumber = driverPhoneNumber; }

    public String getVehicleMake() { return vehicleMake; }
    public void setVehicleMake(String vehicleMake) { this.vehicleMake = vehicleMake; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public String getVehicleLicensePlate() { return vehicleLicensePlate; }
    public void setVehicleLicensePlate(String vehicleLicensePlate) { this.vehicleLicensePlate = vehicleLicensePlate; }

    public String getVehicleColor() { return vehicleColor; }
    public void setVehicleColor(String vehicleColor) { this.vehicleColor = vehicleColor; }

    public Integer getDriverRating() { return driverRating; }
    public void setDriverRating(Integer driverRating) { this.driverRating = driverRating; }

    public Integer getCarRating() { return carRating; }
    public void setCarRating(Integer carRating) { this.carRating = carRating; }

    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }

    public boolean isHasReview() { return hasReview; }
    public void setHasReview(boolean hasReview) { this.hasReview = hasReview; }

    public List<ReportInfo> getReports() { return reports; }
    public void setReports(List<ReportInfo> reports) { this.reports = reports; }

    public List<Coordinates> getCheckpoints() { return checkpoints; }
    public void setCheckpoints(List<Coordinates> checkpoints) { this.checkpoints = checkpoints; }

    public List<DestinationDetail> getDestinations() { return destinations; }
    public void setDestinations(List<DestinationDetail> destinations) { this.destinations = destinations; }
}
