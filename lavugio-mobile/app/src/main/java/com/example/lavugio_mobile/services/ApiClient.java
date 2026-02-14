package com.example.lavugio_mobile.services;

import androidx.annotation.NonNull;

import com.example.lavugio_mobile.BuildConfig;
import com.example.lavugio_mobile.services.auth.AuthApi;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://" + BuildConfig.SERVER_IP + ":8080/";
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static TokenProvider tokenProvider = null;

    public static void init(TokenProvider provider) {
        tokenProvider = provider;
        retrofit = null; // force rebuild on next call
    }

    public interface TokenProvider {
        String getToken();
    }

    public static Retrofit getInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            // Attach auth token to every request automatically
            httpClient.addInterceptor(new Interceptor() {
                @NonNull
                @Override
                public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
                    Request.Builder requestBuilder = chain.request().newBuilder();
                    if (tokenProvider != null) {
                        String token = tokenProvider.getToken();
                        if (token != null) {
                            requestBuilder.addHeader("Authorization", "Bearer " + token);
                        }
                    }
                    return chain.proceed(requestBuilder.build());
                }
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static AuthApi getAuthApi() {
        return getInstance().create(AuthApi.class);
    }
}
