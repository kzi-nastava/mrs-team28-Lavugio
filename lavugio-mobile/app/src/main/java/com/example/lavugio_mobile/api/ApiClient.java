package com.example.lavugio_mobile.api;

import androidx.annotation.NonNull;

import com.example.lavugio_mobile.BuildConfig;
import com.example.lavugio_mobile.api.AuthApi;
import com.example.lavugio_mobile.api.DriverApi;
import com.example.lavugio_mobile.api.LocalDateTimeAdapter;
import com.example.lavugio_mobile.api.RideApi;
import com.example.lavugio_mobile.api.AuthApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.time.LocalDateTime;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://" + BuildConfig.SERVER_IP + ":8080/";
    private static TokenProvider tokenProvider = null;

    // ── Token Provider ───────────────────────────────────

    public interface TokenProvider {
        String getToken();
    }

    /**
     * Call once in Application.onCreate() to wire up auth token injection.
     */
    public static void init(TokenProvider provider) {
        tokenProvider = provider;
        retrofit = null; // force rebuild on next call
    }

    // ── Singleton Retrofit Instance ──────────────────────

    public static Retrofit getInstance() {
        if (retrofit == null) {

            // Logging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // OkHttp client with auth interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(new Interceptor() {
                        @NonNull
                        @Override
                        public Response intercept(@NonNull Chain chain) throws IOException {
                            Request.Builder requestBuilder = chain.request().newBuilder();
                            if (tokenProvider != null) {
                                String token = tokenProvider.getToken();
                                if (token != null) {
                                    requestBuilder.addHeader("Authorization", "Bearer " + token);
                                }
                            }
                            return chain.proceed(requestBuilder.build());
                        }
                    })
                    .build();

            // Gson with LocalDateTime support
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    // ── API Accessors ────────────────────────────────────

    public static AuthApi getAuthApi() {
        return getInstance().create(AuthApi.class);
    }

    public static RideApi getRideApi() {
        return getInstance().create(RideApi.class);
    }

    public static DriverApi getDriverApi() {
        return getInstance().create(DriverApi.class);
    }
}