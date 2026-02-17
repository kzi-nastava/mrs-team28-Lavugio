package com.example.lavugio_mobile.models;

import java.util.List;

public class AdminHistoryDetailedModel {
    private Long rideId;
    private String start;
    private String end;
    private String departure;
    private String destination;
    private double price;
    private boolean cancelled;
    private String cancelledBy;
    private boolean panic;

    // Driver info
    private Long driverId;
    private String driverName;
    private String driverLastName;
    private String driverPhotoPath;
    private String driverPhoneNumber;
    private String driverEmail;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleLicensePlate;
    private String vehicleColor;

    // Passengers
    private List<PassengerInfo> passengers;

    // Review info
    private Integer driverRating;
    private Integer carRating;
    private String reviewComment;
    private boolean hasReview;

    // Reports
    private List<ReportInfo> reports;

    // Checkpoints for map
    private Coordinates[] checkpoints;

    // Full destination info for reordering
    private List<DestinationDetail> destinations;

    public AdminHistoryDetailedModel() {
    }

    // Getters and Setters
    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
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

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverLastName() {
        return driverLastName;
    }

    public void setDriverLastName(String driverLastName) {
        this.driverLastName = driverLastName;
    }

    public String getDriverPhotoPath() {
        return driverPhotoPath;
    }

    public void setDriverPhotoPath(String driverPhotoPath) {
        this.driverPhotoPath = driverPhotoPath;
    }

    public String getDriverPhoneNumber() {
        return driverPhoneNumber;
    }

    public void setDriverPhoneNumber(String driverPhoneNumber) {
        this.driverPhoneNumber = driverPhoneNumber;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public String getVehicleMake() {
        return vehicleMake;
    }

    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleLicensePlate() {
        return vehicleLicensePlate;
    }

    public void setVehicleLicensePlate(String vehicleLicensePlate) {
        this.vehicleLicensePlate = vehicleLicensePlate;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public List<PassengerInfo> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerInfo> passengers) {
        this.passengers = passengers;
    }

    public Integer getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(Integer driverRating) {
        this.driverRating = driverRating;
    }

    public Integer getCarRating() {
        return carRating;
    }

    public void setCarRating(Integer carRating) {
        this.carRating = carRating;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public boolean isHasReview() {
        return hasReview;
    }

    public void setHasReview(boolean hasReview) {
        this.hasReview = hasReview;
    }

    public List<ReportInfo> getReports() {
        return reports;
    }

    public void setReports(List<ReportInfo> reports) {
        this.reports = reports;
    }

    public Coordinates[] getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(Coordinates[] checkpoints) {
        this.checkpoints = checkpoints;
    }

    public List<DestinationDetail> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<DestinationDetail> destinations) {
        this.destinations = destinations;
    }

    // Inner classes
    public static class PassengerInfo {
        private Long passengerId;
        private String passengerName;
        private String passengerLastName;
        private String passengerEmail;

        public PassengerInfo() {
        }

        public Long getPassengerId() {
            return passengerId;
        }

        public void setPassengerId(Long passengerId) {
            this.passengerId = passengerId;
        }

        public String getPassengerName() {
            return passengerName;
        }

        public void setPassengerName(String passengerName) {
            this.passengerName = passengerName;
        }

        public String getPassengerLastName() {
            return passengerLastName;
        }

        public void setPassengerLastName(String passengerLastName) {
            this.passengerLastName = passengerLastName;
        }

        public String getPassengerEmail() {
            return passengerEmail;
        }

        public void setPassengerEmail(String passengerEmail) {
            this.passengerEmail = passengerEmail;
        }
    }

    public static class ReportInfo {
        private Long reportId;
        private String reportMessage;
        private String reporterName;

        public ReportInfo() {
        }

        public Long getReportId() {
            return reportId;
        }

        public void setReportId(Long reportId) {
            this.reportId = reportId;
        }

        public String getReportMessage() {
            return reportMessage;
        }

        public void setReportMessage(String reportMessage) {
            this.reportMessage = reportMessage;
        }

        public String getReporterName() {
            return reporterName;
        }

        public void setReporterName(String reporterName) {
            this.reporterName = reporterName;
        }
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

        public DestinationDetail() {
        }

        public int getOrderIndex() {
            return orderIndex;
        }

        public void setOrderIndex(int orderIndex) {
            this.orderIndex = orderIndex;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getStreetName() {
            return streetName;
        }

        public void setStreetName(String streetName) {
            this.streetName = streetName;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getStreetNumber() {
            return streetNumber;
        }

        public void setStreetNumber(String streetNumber) {
            this.streetNumber = streetNumber;
        }

        public int getZipCode() {
            return zipCode;
        }

        public void setZipCode(int zipCode) {
            this.zipCode = zipCode;
        }
    }
}
