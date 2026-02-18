package com.example.lavugio_mobile.api;


import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApi {

    // ── Profile ──────────────────────────────────────────

//    @GET("api/users/profile")
//    Call<UserProfile> getUserProfile();

//    @PUT("api/users/profile")
//    Call<Object> updateProfile(@Body EditProfileDTO updatedProfile);

    @Multipart
    @POST("api/users/profile-photo")
    Call<Object> uploadProfilePicture(@Part MultipartBody.Part file);

    // ── Password ─────────────────────────────────────────

    @PUT("api/users/change-password")
    Call<ResponseBody> changePassword(@Body Map<String, String> passwordData);

    // ── Activation ───────────────────────────────────────

    @POST("api/drivers/activate-account")
    Call<Object> activateAccount(@Body Map<String, String> activationData);

    @GET("api/users/activate/validate")
    Call<Object> validateActivationToken(@Query("token") String token);

    // ── Email Search ─────────────────────────────────────

    @GET("api/users/email-suggestions")
    Call<List<EmailSuggestion>> searchUserEmails(@Query("query") String query);

    // ── Blocking ─────────────────────────────────────────

    @POST("api/users/block")
    Call<Object> blockUser(@Body Map<String, String> blockData);

    @GET("api/users/is-blocked")
    Call<BlockStatus> isUserBlocked();

    @GET("api/users/can-order-ride")
    Call<CanOrderRideStatus> canUserOrderRide();

    // ── Ride Related ─────────────────────────────────────

//    @GET("api/regularUsers/latest-ride")
//    Call<LatestRideModel> getLatestRideId();

//    @GET("api/regularUsers/history")
//    Call<RideHistoryUserPagingModel> getUserRideHistory(
//            @Query("page") int page,
//            @Query("pageSize") int pageSize,
//            @Query("sorting") String sorting,
//            @Query("sortBy") String sortBy,
//            @Query("startDate") String startDate,
//            @Query("endDate") String endDate
//    );

//    @GET("api/regularUsers/history/{rideId}")
//    Call<RideHistoryUserDetailedModel> getUserRideHistoryDetailed(
//            @Path("rideId") long rideId
//    );

    // ── Chat ─────────────────────────────────────────────

//    @GET("api/users/chattable")
//    Call<List<UserChatModel>> getChattableUsers();

    // ── Helper Classes ───────────────────────────────────

    class EmailSuggestion {
        public String email;
    }

    class BlockStatus {
        public boolean isBlocked;
        public String reason;

        public BlockStatus() {
        }

        public BlockStatus(boolean isBlocked, String reason) {
            this.isBlocked = isBlocked;
            this.reason = reason;
        }

        private boolean isBlocked() {
            return isBlocked;
        }

        private String getReason() {
            return reason;
        }
    }

    class CanOrderRideStatus {
        public boolean isInRide;
        public BlockStatus block;
    }
}