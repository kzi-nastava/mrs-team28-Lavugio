package com.example.lavugio_mobile.services.utils;

import com.example.lavugio_mobile.data.model.utils.PhotonResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PhotonApiService {

    @GET("api/")
    Call<PhotonResponse> searchAddress(
            @Query("q") String query,
            @Query("limit") int limit,
            @Query("lat") Double lat,
            @Query("lon") Double lon,
            @Query("lang") String language
    );

    @GET("reverse")
    Call<PhotonResponse> reverseGeocode(
            @Query("lat") double latitude,
            @Query("lon") double longitude
    );
}