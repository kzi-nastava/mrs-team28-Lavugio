package com.example.lavugio_mobile.repository.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lavugio_mobile.models.user.DriverUpdateRequestDiffDTO;
import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.services.user.AdminApi;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class DriverUpdateRequestsRepository {
    private final AdminApi adminApi;

    public DriverUpdateRequestsRepository() {
        adminApi = ApiClient.getInstance().create(AdminApi.class);
    }

    public LiveData<List<DriverUpdateRequestDiffDTO>> getDriverUpdateRequests() {
        MutableLiveData<List<DriverUpdateRequestDiffDTO>> result = new MutableLiveData<>();

        adminApi.getDriverEditRequests().enqueue(new Callback<List<DriverUpdateRequestDiffDTO>>() {
            @Override
            public void onResponse(Call<List<DriverUpdateRequestDiffDTO>> call, Response<List<DriverUpdateRequestDiffDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<DriverUpdateRequestDiffDTO>> call, Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<Boolean> approveEditRequest(Long requestId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        adminApi.approveEditRequest(requestId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                result.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.setValue(false);
            }
        });
        return result;
    }

    public LiveData<Boolean> rejectEditRequest(Long requestId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        adminApi.rejectEditRequest(requestId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
