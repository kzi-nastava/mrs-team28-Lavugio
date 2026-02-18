package com.example.lavugio_mobile.api;

import com.example.lavugio_mobile.data.model.route.FavoriteRoute;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FavoriteRouteApi {
    @POST("api/favorite-routes/add")
    Call<Void> createFavoriteRoute(@Body FavoriteRoute request);

    @GET("api/favorite-routes")
    Call<List<FavoriteRoute>> getFavoriteRoutes();

    @DELETE("api/favorite-routes/{id}")
    Call<Void> deleteFavoriteRoute(@Path("id") String routeId);
}
