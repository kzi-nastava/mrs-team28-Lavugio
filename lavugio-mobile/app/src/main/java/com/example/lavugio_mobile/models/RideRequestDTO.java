package com.example.lavugio_mobile.models;

import java.util.List;

public class RideRequestDTO {
    private List<DestinationDTO> destinations;
    private List<String> passengerEmails;
    private String vehicleType;
    private boolean babyFriendly;
    private boolean petFriendly;
    private String scheduledTime;
    private boolean scheduled;
    private int estimatedDurationSeconds;
    private double distance;
    private double price;

    // Nested classes
    public static class LocationDTO {
        private int orderIndex;
        private double latitude;
        private double longitude;

        public int getOrderIndex() { return orderIndex; }
        public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }

        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
    }

    public static class DestinationDTO {
        private LocationDTO location;
        private String address;
        private String streetName;
        private String city;
        private String country;
        private String streetNumber;
        private int zipCode;

        public LocationDTO getLocation() { return location; }
        public void setLocation(LocationDTO location) { this.location = location; }

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

    // Getters and Setters
    public List<DestinationDTO> getDestinations() { return destinations; }
    public void setDestinations(List<DestinationDTO> destinations) { this.destinations = destinations; }

    public List<String> getPassengerEmails() { return passengerEmails; }
    public void setPassengerEmails(List<String> passengerEmails) { this.passengerEmails = passengerEmails; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public boolean isBabyFriendly() { return babyFriendly; }
    public void setBabyFriendly(boolean babyFriendly) { this.babyFriendly = babyFriendly; }

    public boolean isPetFriendly() { return petFriendly; }
    public void setPetFriendly(boolean petFriendly) { this.petFriendly = petFriendly; }

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    public boolean isScheduled() { return scheduled; }
    public void setScheduled(boolean scheduled) { this.scheduled = scheduled; }

    public int getEstimatedDurationSeconds() { return estimatedDurationSeconds; }
    public void setEstimatedDurationSeconds(int estimatedDurationSeconds) { this.estimatedDurationSeconds = estimatedDurationSeconds; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}