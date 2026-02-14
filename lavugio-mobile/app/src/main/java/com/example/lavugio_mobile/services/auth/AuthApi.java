package com.example.lavugio_mobile.services.auth;

import com.example.lavugio_mobile.models.auth.LoginRequest;
import com.example.lavugio_mobile.models.auth.LoginResponse;
import com.example.lavugio_mobile.models.auth.VerifyEmailRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface AuthApi {

    /**
     * Register WITHOUT a profile picture.
     * Backend uses @RequestParam, so we send multipart form data.
     */
    @Multipart
    @POST("api/regularUsers/register")
    Call<Void> register(
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("name") RequestBody name,
            @Part("lastName") RequestBody lastName,
            @Part("phoneNumber") RequestBody phoneNumber,
            @Part("address") RequestBody address
    );

    /**
     * Register WITH a profile picture.
     */
    @Multipart
    @POST("api/regularUsers/register")
    Call<Void> registerWithFile(
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("name") RequestBody name,
            @Part("lastName") RequestBody lastName,
            @Part("phoneNumber") RequestBody phoneNumber,
            @Part("address") RequestBody address,
            @Part MultipartBody.Part profilePicture
    );

    @POST("api/regularUsers/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/regularUsers/verify-email")
    Call<Void> verifyEmail(@Body VerifyEmailRequest request);

    @POST("api/regularUsers/logout/{userId}")
    Call<Void> logout(@Path("userId") int userId);
}