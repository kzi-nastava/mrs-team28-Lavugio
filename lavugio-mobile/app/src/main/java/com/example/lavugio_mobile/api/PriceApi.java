package com.example.lavugio_mobile.api;

import com.example.lavugio_mobile.models.RidePriceModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PriceApi {
    
    @GET("/api/rides/prices")
    Call<RidePriceModel> getPrices();
    
    @POST("/api/rides/prices")
    Call<Void> postPrices(@Body RidePriceModel prices);
}