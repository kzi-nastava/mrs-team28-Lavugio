package com.example.lavugio_mobile.repository.ride;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.api.FavoriteRouteApi;
import com.example.lavugio_mobile.api.RideApi;
import com.example.lavugio_mobile.data.model.route.FavoriteRoute;
import com.example.lavugio_mobile.data.model.utils.ErrorResponse;
import com.example.lavugio_mobile.data.model.utils.ResultState;
import com.example.lavugio_mobile.models.RidePriceEstimateDTO;
import com.example.lavugio_mobile.models.RideRequestDTO;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindRideRepository {
    private final RideApi rideApi;
    private final FavoriteRouteApi favoriteRouteApi;

    public FindRideRepository() {
        rideApi = ApiClient.getInstance().create(RideApi.class);
        favoriteRouteApi = ApiClient.getInstance().create(FavoriteRouteApi.class);
    }

    public LiveData<Double> estimatePrice(RidePriceEstimateDTO request) {
        MutableLiveData<Double> result = new MutableLiveData<>();
        rideApi.estimatePrice(request).enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                if (response.isSuccessful()) {
                    result.setValue(response.body());
                } else {
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Double> call, Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<ResultState> findRide(RideRequestDTO requestDTO) {
        MutableLiveData<ResultState> result = new MutableLiveData<>();
        rideApi.findRide(requestDTO).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    result.setValue(new ResultState.Success());
                } else {
                    String errorMessage = "Unknown error";

                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    result.setValue(new ResultState.Error(errorMessage));
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                result.setValue(
                        new ResultState.Error(
                                t.getMessage() != null ? t.getMessage() : "Network error"
                        )
                );
            }
        });
        return result;
    }

    public LiveData<ResultState> createFavoriteRoute(FavoriteRoute request) {

        MutableLiveData<ResultState> result = new MutableLiveData<>();

        favoriteRouteApi.createFavoriteRoute(request)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if (response.isSuccessful()) {
                            result.setValue(new ResultState.Success());
                        } else {

                            String errorMessage = "Unknown error";

                            try {
                                if (response.errorBody() != null) {
                                    String errorJson = response.errorBody().string();

                                    Gson gson = new Gson();
                                    ErrorResponse error =
                                            gson.fromJson(errorJson, ErrorResponse.class);

                                    errorMessage = error.getMessage();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            result.setValue(new ResultState.Error(errorMessage));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        result.setValue(
                                new ResultState.Error(
                                        t.getMessage() != null ? t.getMessage() : "Network error"
                                )
                        );
                    }
                });

        return result;
    }



    public LiveData<FavoriteRoute[]> getFavoriteRoutes() {
        MutableLiveData<FavoriteRoute[]> result = new MutableLiveData<>();
        favoriteRouteApi.getFavoriteRoutes().enqueue(new Callback<List<FavoriteRoute>>() {
            @Override
            public void onResponse(Call<List<FavoriteRoute>> call, Response<List<FavoriteRoute>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FavoriteRoute> routes = response.body();
                    result.setValue(routes.toArray(new FavoriteRoute[0]));
                } else {
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<java.util.List<FavoriteRoute>> call, Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<ResultState> deleteFavoriteRoute(String routeId) {
        MutableLiveData<ResultState> result = new MutableLiveData<>();
        favoriteRouteApi.deleteFavoriteRoute(routeId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(new ResultState.Success());
                } else {
                    result.setValue(new ResultState.Error("Failed to delete route"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(
                        new ResultState.Error(
                                t.getMessage() != null ? t.getMessage() : "Network error"
                        )
                );
            }
        });
        return result;
    }

}
