package com.example.lavugio_mobile.services.utils;

import android.os.Handler;
import android.os.Looper;

import com.example.lavugio_mobile.data.model.utils.PhotonResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeocodingHelper {

    private PhotonApiService apiService;
    private Handler mainHandler;

    public interface GeocodingCallback {
        void onSuccess(List<GeocodingResult> results);
        void onError(String error);
    }

    public GeocodingHelper() {
        apiService = RetrofitClient.getInstance().getPhotonApiService();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Search for addresses using Photon API
     */
    public void searchAddress(String query, GeocodingCallback callback) {
        if (query == null || query.trim().isEmpty()) {
            mainHandler.post(() -> callback.onSuccess(new ArrayList<>()));
            return;
        }

        Call<PhotonResponse> call = apiService.searchAddress(query, 5);

        call.enqueue(new Callback<PhotonResponse>() {
            @Override
            public void onResponse(Call<PhotonResponse> call, Response<PhotonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GeocodingResult> results = parsePhotonResponse(response.body());
                    mainHandler.post(() -> callback.onSuccess(results));
                } else {
                    mainHandler.post(() -> callback.onError("Request failed: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<PhotonResponse> call, Throwable t) {
                mainHandler.post(() -> callback.onError("Network error: " + t.getMessage()));
            }
        });
    }

    /**
     * Parse Photon API response
     */
    private List<GeocodingResult> parsePhotonResponse(PhotonResponse photonResponse) {
        List<GeocodingResult> results = new ArrayList<>();

        if (photonResponse.getFeatures() == null) {
            return results;
        }

        for (PhotonResponse.Feature feature : photonResponse.getFeatures()) {
            PhotonResponse.Properties props = feature.getProperties();
            PhotonResponse.Geometry geom = feature.getGeometry();

            String name = props.getName() != null ? props.getName() : "";
            String street = props.getStreet() != null ? props.getStreet() : "";
            String houseNumber = props.getHouseNumber() != null ? props.getHouseNumber() : "";
            String city = props.getCity() != null ? props.getCity() : "";
            String country = props.getCountry() != null ? props.getCountry() : "";
            String postcode = props.getPostcode() != null ? props.getPostcode() : "";

            double lat = geom.getLatitude();
            double lon = geom.getLongitude();

            // Build display address to match web format: street + housenumber, city OR city OR name
            StringBuilder displayAddress = new StringBuilder();

            if (!street.isEmpty()) {
                displayAddress.append(street);
                if (!houseNumber.isEmpty()) {
                    displayAddress.append(" ").append(houseNumber);
                }
                if (!city.isEmpty()) {
                    displayAddress.append(", ").append(city);
                }
            } else if (!city.isEmpty()) {
                displayAddress.append(city);
            } else if (!name.isEmpty()) {
                displayAddress.append(name);
            } else {
                displayAddress.append("Unknown location");
            }

            GeocodingResult result = new GeocodingResult(
                    displayAddress.toString(),
                    lat,
                    lon,
                    street,
                    houseNumber,
                    city,
                    postcode,
                    country
            );

            results.add(result);
        }

        return results;
    }

    /**
     * Cancel all pending requests
     */
    public void cancelRequests() {
        // Retrofit automatically cancels requests when call is garbage collected
    }

    /**
     * Geocoding result model
     */
    public static class GeocodingResult {
        private String displayName;
        private double latitude;
        private double longitude;
        private String street;
        private String houseNumber;
        private String city;
        private String postcode;
        private String country;

        public GeocodingResult(String displayName, double latitude, double longitude,
                               String street, String houseNumber, String city,
                               String postcode, String country) {
            this.displayName = displayName;
            this.latitude = latitude;
            this.longitude = longitude;
            this.street = street;
            this.houseNumber = houseNumber;
            this.city = city;
            this.postcode = postcode;
            this.country = country;
        }

        public String getDisplayName() { return displayName; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getStreet() { return street; }
        public String getHouseNumber() { return houseNumber; }
        public String getCity() { return city; }
        public String getPostcode() { return postcode; }
        public String getCountry() { return country; }

        @Override
        public String toString() {
            return displayName;
        }
    }
}