package com.example.lavugio_mobile.services.user;

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
}
