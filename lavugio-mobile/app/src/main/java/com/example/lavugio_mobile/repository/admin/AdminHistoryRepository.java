package com.example.lavugio_mobile.repository.admin;

import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.models.AdminHistoryDetailedModel;
import com.example.lavugio_mobile.models.AdminHistoryPagingModel;
import com.example.lavugio_mobile.services.user.AdminApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminHistoryRepository {
    private final AdminApi adminApi;

    public AdminHistoryRepository() {
        adminApi = ApiClient.getInstance().create(AdminApi.class);
    }

    public interface AdminHistoryCallback<T> {
        void onSuccess(T data);
        void onError(int code, String message);
    }

    public void getUserHistory(String email, int page, int pageSize, String sorting, String sortBy,
                               String startDate, String endDate, AdminHistoryCallback<AdminHistoryPagingModel> callback) {
        adminApi.getUserHistory(email, page, pageSize, sorting, sortBy, startDate, endDate)
                .enqueue(new Callback<AdminHistoryPagingModel>() {
                    @Override
                    public void onResponse(Call<AdminHistoryPagingModel> call, Response<AdminHistoryPagingModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError(response.code(), "Failed to load history");
                        }
                    }

                    @Override
                    public void onFailure(Call<AdminHistoryPagingModel> call, Throwable t) {
                        callback.onError(-1, t.getMessage());
                    }
                });
    }

    public void getRideDetails(Long rideId, AdminHistoryCallback<AdminHistoryDetailedModel> callback) {
        adminApi.getRideDetails(rideId).enqueue(new Callback<AdminHistoryDetailedModel>() {
            @Override
            public void onResponse(Call<AdminHistoryDetailedModel> call, Response<AdminHistoryDetailedModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(response.code(), "Failed to load ride details");
                }
            }

            @Override
            public void onFailure(Call<AdminHistoryDetailedModel> call, Throwable t) {
                callback.onError(-1, t.getMessage());
            }
        });
    }
}
