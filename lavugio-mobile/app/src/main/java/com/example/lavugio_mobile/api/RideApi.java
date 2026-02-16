package com.example.lavugio_mobile.api;

import com.example.lavugio_mobile.models.ActiveRide;
import com.example.lavugio_mobile.models.FinishRide;
import com.example.lavugio_mobile.models.RideEstimateRequest;
import com.example.lavugio_mobile.models.RideMonitoringModel;
import com.example.lavugio_mobile.models.RideOverviewModel;
import com.example.lavugio_mobile.models.RideReport;
import com.example.lavugio_mobile.models.RideRequestDTO;
import com.example.lavugio_mobile.models.RideReview;
import com.example.lavugio_mobile.models.ScheduleRideRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RideApi {

    // ── Ride Overview ────────────────────────────────────

    @GET("api/rides/{rideId}/overview")
    Call<RideOverviewModel> getRideOverview(@Path("rideId") long rideId);

    @GET("api/rides/{rideId}/overview/updated")
    Call<RideOverviewModel> getUpdatedRideOverview(@Path("rideId") long rideId);

    // ── Ride Actions ─────────────────────────────────────

    @POST("api/rides/{rideId}/start")
    Call<Void> startRide(@Path("rideId") long rideId);

    @PUT("api/rides/finish")
    Call<FinishRide> finishRide(@Body FinishRide finish);

    @POST("api/rides/{rideId}/cancel-by-passenger")
    Call<Void> cancelRideByPassenger(@Path("rideId") long rideId);

    @POST("api/rides/{rideId}/cancel-by-driver")
    Call<Void> cancelRideByDriver(
            @Path("rideId") long rideId,
            @Body Map<String, String> reason
    );

    @POST("api/rides/{rideId}/panic")
    Call<Void> triggerPanic(
            @Path("rideId") long rideId,
            @Body Object panicAlert
    );

    // ── Report & Review ──────────────────────────────────

    @POST("api/rides/report")
    Call<RideReport> postRideReport(@Body RideReport report);

    @POST("api/rides/{rideId}/review")
    Call<Void> postRideReview(
            @Path("rideId") long rideId,
            @Body RideReview review
    );

    // ── Ride Booking ─────────────────────────────────────

    @POST("api/rides/estimate-price")
        Call<Double> estimatePrice(@Body com.example.lavugio_mobile.models.RidePriceEstimateDTO request);

    @POST("api/rides/schedule")
    Call<Object> scheduleRide(@Body ScheduleRideRequest request);

    @POST("api/rides/find-ride")
    Call<Object> findRide(@Body RideRequestDTO request);

    // ── Access Check ─────────────────────────────────────

    @GET("api/rides/{rideId}/access")
    Call<Boolean> canAccess(@Path("rideId") long rideId);

    // ── Active Rides ─────────────────────────────────────

    @GET("api/rides/active")
    Call<List<RideMonitoringModel>> getActiveRides();

    @GET("api/rides/user/active")
    Call<List<ActiveRide>> getUserActiveRides(@Query("t") long timestamp);
}