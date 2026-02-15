package com.example.lavugio_mobile.services.user;

import com.example.lavugio_mobile.models.user.UserProfileData;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UserApi {

    @GET("api/users/profile")
    Call<UserProfileData> getProfile();
}
