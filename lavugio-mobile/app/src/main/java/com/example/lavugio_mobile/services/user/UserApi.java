package com.example.lavugio_mobile.services.user;

import com.example.lavugio_mobile.models.Coordinates;
import com.example.lavugio_mobile.models.user.ChangePasswordDTO;
import com.example.lavugio_mobile.models.user.DriverActiveTimeResponse;
import com.example.lavugio_mobile.models.user.DriverEditProfileRequestDTO;
import com.example.lavugio_mobile.models.user.EditProfileDTO;
import com.example.lavugio_mobile.models.user.UserProfileData;

import java.sql.Blob;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface UserApi {

    @GET("api/users/profile")
    Call<UserProfileData> getProfile();

    @GET("api/users/profile-photo")
    Call<ResponseBody> getProfilePhoto();

    @POST("api/drivers/edit-request")
    Call<ResponseBody> sendDriverEditRequest(@Body DriverEditProfileRequestDTO driverEditProfileRequestDTO);

    @PUT("api/users/profile")
    Call<ResponseBody> sendProfileEditRequest(@Body EditProfileDTO editProfileDTO);

    @Multipart
    @POST("api/users/profile-photo")
    Call<ResponseBody> uploadProfilePhoto(@Part MultipartBody.Part file);

    @PUT("api/users/change-password")
    Call<ResponseBody> changePassword(@Body ChangePasswordDTO changePasswordDTO);

    @GET("api/drivers/active-24h")
    Call<DriverActiveTimeResponse> getDriverActiveLast24Hours();

    @POST("api/drivers/activate")
    Call<ResponseBody> activateDriver(@Body Coordinates coordinates);

    @POST("api/drivers/deactivate")
    Call<ResponseBody> deactivateDriver();

    @GET("api/drivers/{id}")
    Call<ResponseBody> getDriverStatus(@retrofit2.http.Path("id") int driverId);

    // ── Regular User Ride History ────────────────────────

    @GET("api/regularUsers/history")
    Call<com.example.lavugio_mobile.models.RideHistoryUserPagingModel> getUserRideHistory(
            @retrofit2.http.Query("page") int page,
            @retrofit2.http.Query("pageSize") int pageSize,
            @retrofit2.http.Query("sorting") String sorting,
            @retrofit2.http.Query("sortBy") String sortBy,
            @retrofit2.http.Query("startDate") String startDate,
            @retrofit2.http.Query("endDate") String endDate
    );

    @GET("api/regularUsers/history/{rideId}")
    Call<com.example.lavugio_mobile.models.RideHistoryUserDetailedModel> getUserRideHistoryDetailed(
            @retrofit2.http.Path("rideId") long rideId
    );

    @GET("api/users/can-order-ride")
    Call<com.example.lavugio_mobile.models.CanOrderRideResponse> canUserOrderRide();
}
