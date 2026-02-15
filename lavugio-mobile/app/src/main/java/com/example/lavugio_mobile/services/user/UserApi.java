package com.example.lavugio_mobile.services.user;

import com.example.lavugio_mobile.models.user.UserProfileData;

import java.sql.Blob;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface UserApi {

    @GET("api/users/profile")
    Call<UserProfileData> getProfile();

    @GET("api/users/profile-photo")
    Call<ResponseBody> getProfilePhoto();
}
