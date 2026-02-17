package com.example.lavugio_mobile.data.model.route;
public class RideDestination {
    private String name;
    private String street;
    private String houseNumber;
    private String city;
    private String country;
    private Coordinates coordinates;

    public RideDestination() {
    }

    public RideDestination(String name, String street, String houseNumber, String city, String country, Coordinates coordinates) {
        this.name = name;
        this.street = street;
        this.houseNumber = houseNumber;
        this.city = city;
        this.country = country;
        this.coordinates = coordinates;
    }

    public String getName() {
        return this.street + " " + this.houseNumber + ", " + this.city;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
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

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
}
