package com.example.lavugio_mobile.repository.reports;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.api.RidesReportsApi;
import com.example.lavugio_mobile.data.model.RidesReportsAdminFiltersDTO;
import com.example.lavugio_mobile.data.model.reports.RidesReportsDateRangeDTO;
import com.example.lavugio_mobile.data.model.reports.RidesReportsResponseDTO;
import com.example.lavugio_mobile.data.model.utils.ResultState;
import com.example.lavugio_mobile.models.RideRequestDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RidesReportsRepository {
    private final RidesReportsApi ridesReportsApi;

    public RidesReportsRepository() {
        ridesReportsApi = ApiClient.getInstance().create(RidesReportsApi.class);
    }

    public LiveData<RidesReportsResponseDTO> getDriverReport(RidesReportsDateRangeDTO dateRange) {
        MutableLiveData<RidesReportsResponseDTO> result = new MutableLiveData<>();
        ridesReportsApi.generateDriverReport(dateRange).enqueue(new Callback<RidesReportsResponseDTO>() {
            @Override
            public void onResponse(Call<RidesReportsResponseDTO> call, Response<RidesReportsResponseDTO> response) {
                if (response.isSuccessful()) {
                    result.setValue(response.body());
                } else {
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<RidesReportsResponseDTO> call, Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<RidesReportsResponseDTO> getRegularUserReport(RidesReportsDateRangeDTO dateRange) {
        MutableLiveData<RidesReportsResponseDTO> result = new MutableLiveData<>();
        ridesReportsApi.generateRegularUserReport(dateRange).enqueue(new Callback<RidesReportsResponseDTO>() {
            @Override
            public void onResponse(Call<RidesReportsResponseDTO> call, Response<RidesReportsResponseDTO> response) {
                if (response.isSuccessful()) {
                    result.setValue(response.body());
                } else {
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<RidesReportsResponseDTO> call, Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<RidesReportsResponseDTO> getAdministratorReport(RidesReportsAdminFiltersDTO adminFilters) {
        MutableLiveData<RidesReportsResponseDTO> result = new MutableLiveData<>();
        ridesReportsApi.generateAdministratorReport(adminFilters).enqueue(new Callback<RidesReportsResponseDTO>() {
            @Override
            public void onResponse(Call<RidesReportsResponseDTO> call, Response<RidesReportsResponseDTO> response) {
                if (response.isSuccessful()) {
                    result.setValue(response.body());
                } else {
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<RidesReportsResponseDTO> call, Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

}
