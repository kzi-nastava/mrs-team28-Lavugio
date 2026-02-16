package com.example.lavugio_mobile.services.user;

import com.example.lavugio_mobile.models.user.BlockUserRequest;
import com.example.lavugio_mobile.models.user.DriverUpdateRequestDiffDTO;
import com.example.lavugio_mobile.models.user.EmailSuggestion;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminApi {
    @GET("api/drivers/edit-requests")
    Call<List<DriverUpdateRequestDiffDTO>> getDriverEditRequests();

    @POST("api/drivers/edit-requests/{requestId}/approve")
    Call<ResponseBody> approveEditRequest(@Path("requestId") long requestId);

    @POST("api/drivers/edit-requests/{requestId}/reject")
    Call<ResponseBody> rejectEditRequest(@Path("requestId") long requestId);

    @POST("api/users/block")
    Call<ResponseBody> blockUser(@Body BlockUserRequest request);

    @GET("api/users/email-suggestions")
    Call<List<EmailSuggestion>> getEmailSuggestions(@Query("query") String query);
}
