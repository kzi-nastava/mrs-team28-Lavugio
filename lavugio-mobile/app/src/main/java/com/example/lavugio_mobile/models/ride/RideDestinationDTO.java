package com.example.lavugio_mobile.models.ride;

public class RideDestinationDTO {
    private StopBaseDTO location;
    private String address;

    private String streetName;
    private String city;
    private String country;
    private String streetNumber;
    private int zipCode;

    public RideDestinationDTO() {
    }

    public RideDestinationDTO(StopBaseDTO location, String address) {
        this.location = location;
        this.address = address;
    }

    public StopBaseDTO getLocation() {
        return location;
    }

    public void setLocation(StopBaseDTO location) {
        this.location = location;
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
