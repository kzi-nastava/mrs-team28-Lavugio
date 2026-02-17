package com.example.lavugio_mobile.api;

import com.example.lavugio_mobile.data.model.RidesReportsAdminFiltersDTO;
import com.example.lavugio_mobile.data.model.reports.RidesReportsDateRangeDTO;
import com.example.lavugio_mobile.data.model.reports.RidesReportsResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RidesReportsApi {

    @POST("/api/rides-reports/driver")
    Call<RidesReportsResponseDTO> generateDriverReport(@Body RidesReportsDateRangeDTO dateRange);

    @POST("/api/rides-reports/regular-user")
    Call<RidesReportsResponseDTO> generateRegularUserReport(@Body RidesReportsDateRangeDTO dateRange);

    @POST("/api/rides-reports/admin")
    Call<RidesReportsResponseDTO> generateAdministratorReport(@Body RidesReportsAdminFiltersDTO adminFilters);

}
