package com.example.lavugio_mobile.data.model.utils;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PhotonResponse {
    @SerializedName("features")
    private List<Feature> features;

    public List<Feature> getFeatures() {
        return features;
    }

    public static class Feature {
        @SerializedName("properties")
        private Properties properties;

        @SerializedName("geometry")
        private Geometry geometry;

        public Properties getProperties() {
            return properties;
        }

        public Geometry getGeometry() {
            return geometry;
        }
    }

    public static class Properties {
        @SerializedName("name")
        private String name;

        @SerializedName("street")
        private String street;

        @SerializedName("housenumber")
        private String houseNumber;

        @SerializedName("city")
        private String city;

        @SerializedName("country")
        private String country;

        @SerializedName("postcode")
        private String postcode;

        @SerializedName("state")
        private String state;

        // Getters
        public String getName() { return name; }
        public String getStreet() { return street; }
        public String getHouseNumber() { return houseNumber; }
        public String getCity() { return city; }
        public String getCountry() { return country; }
        public String getPostcode() { return postcode; }
        public String getState() { return state; }
    }

    public static class Geometry {
        @SerializedName("coordinates")
        private List<Double> coordinates;

        public List<Double> getCoordinates() {
            return coordinates;
        }

        public double getLongitude() {
            return coordinates != null && coordinates.size() > 0 ? coordinates.get(0) : 0;
        }

        public double getLatitude() {
            return coordinates != null && coordinates.size() > 1 ? coordinates.get(1) : 0;
        }
    }
}
