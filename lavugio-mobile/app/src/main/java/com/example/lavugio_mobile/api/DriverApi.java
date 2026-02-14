package com.example.lavugio_mobile.api;

import com.example.lavugio_mobile.models.Coordinates;
import com.example.lavugio_mobile.models.DriverActiveTimeDTO;
import com.example.lavugio_mobile.models.DriverLocation;
import com.example.lavugio_mobile.models.DriverRegistration;
import com.example.lavugio_mobile.models.DriverUpdateRequestDiffDTO;
import com.example.lavugio_mobile.models.EditDriverProfileRequestDTO;
import com.example.lavugio_mobile.models.RideHistoryDriverDetailedModel;
import com.example.lavugio_mobile.models.RideHistoryDriverPagingModel;
import com.example.lavugio_mobile.models.ScheduledRideModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DriverApi {

    // ── Location ─────────────────────────────────────────

    @GET("api/drivers/locations")
    Call<List<DriverLocation>> getDriverLocations();

    @GET("api/drivers/{driverId}/location")
    Call<DriverLocation> getDriverLocation(@Path("driverId") long driverId);

    @PUT("api/drivers/location")
    Call<DriverLocation> putDriverCoordinates(@Body Coordinates coords);

    // ── Registration ─────────────────────────────────────

    @POST("api/drivers/register")
    Call<Object> registerDriver(@Body DriverRegistration data);

    // ── Profile Edit Requests ────────────────────────────

    @POST("api/drivers/edit-request")
    Call<Object> sendEditRequest(@Body EditDriverProfileRequestDTO request);

    @GET("api/drivers/edit-requests")
    Call<List<DriverUpdateRequestDiffDTO>> getEditRequests();

    @POST("api/drivers/edit-requests/{requestId}/approve")
    Call<Void> approveEditRequest(@Path("requestId") long requestId);

    @POST("api/drivers/edit-requests/{requestId}/reject")
    Call<Void> rejectEditRequest(@Path("requestId") long requestId);

    // ── Scheduled Rides ──────────────────────────────────

    @GET("api/drivers/scheduled-rides")
    Call<List<ScheduledRideModel>> getScheduledRides();

    // ── Ride History ─────────────────────────────────────

    @GET("api/drivers/history")
    Call<RideHistoryDriverPagingModel> getDriverRideHistory(
            @Query("page") int page,
            @Query("pageSize") int pageSize,
            @Query("sorting") String sorting,
            @Query("sortBy") String sortBy,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @GET("api/drivers/history/{rideId}")
    Call<RideHistoryDriverDetailedModel> getDriverRideHistoryDetailed(
            @Path("rideId") long rideId
    );

    // ── Activation ───────────────────────────────────────

    @POST("api/drivers/activate")
    Call<Object> activateDriver(@Body Coordinates coords);

    @POST("api/drivers/deactivate")
    Call<Object> deactivateDriver();

    // ── Active Time ──────────────────────────────────────

    @GET("api/drivers/active-24h")
    Call<DriverActiveTimeDTO> getDriverActiveLast24Hours();
}