package com.example.lavugio_mobile.repository.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lavugio_mobile.models.user.DriverEditProfileRequestDTO;
import com.example.lavugio_mobile.models.user.EditProfileDTO;
import com.example.lavugio_mobile.models.user.UserProfileData;
import com.example.lavugio_mobile.services.ApiClient;
import com.example.lavugio_mobile.services.user.UserApi;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileRepository {

    private final UserApi userApi;

    public ProfileRepository() {
        userApi = ApiClient.getInstance().create(UserApi.class);
    }

    public LiveData<UserProfileData> getProfile() {
        MutableLiveData<UserProfileData> data = new MutableLiveData<>();

        userApi.getProfile().enqueue(new Callback<UserProfileData>() {
            @Override
            public void onResponse(Call<UserProfileData> call,
                                   Response<UserProfileData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<UserProfileData> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<byte[]> getProfilePhoto() {
        MutableLiveData<byte[]> data = new MutableLiveData<>();

        userApi.getProfilePhoto().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        data.setValue(response.body().bytes());
                    } catch (Exception e) {
                        data.setValue(null);
                    }
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<Boolean> uploadProfilePhoto(MultipartBody.Part filePart) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        userApi.uploadProfilePhoto(filePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                result.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.setValue(false);
            }
        });

        return result;
    }

    public LiveData<Boolean> updateProfile(EditProfileDTO dto) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        userApi.sendProfileEditRequest(dto)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        result.setValue(response.isSuccessful());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        result.setValue(false);
                    }
                });

        return result;
    }

    public LiveData<Boolean> sendDriverEditRequest(DriverEditProfileRequestDTO dto) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        userApi.sendDriverEditRequest(dto).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                result.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.setValue(false);
            }
        });

        return result;
    }
}