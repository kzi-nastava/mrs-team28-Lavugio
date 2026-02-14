package com.example.lavugio_mobile.services;

import com.example.lavugio_mobile.models.DriverLocation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DriverService {

    @GET("drivers/locations")
    Call<List<DriverLocation>> getLocations();
}
