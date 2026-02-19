package com.example.lavugio_mobile.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.api.PriceApi;
import com.example.lavugio_mobile.models.RidePriceModel;

import retrofit2.Call;
import retrofit2.Response;

public class PriceService {

    private static final String TAG = "PriceService";

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(int code, String message);
    }

    private final PriceApi api;

    public PriceService() {
        this.api = ApiClient.getPriceApi();
    }

    // ── Get Prices ───────────────────────────────────────

    public void getPrices(Callback<RidePriceModel> callback) {
        Log.d(TAG, "getPrices: Fetching prices from server");
        api.getPrices().enqueue(wrapCallback(callback));
    }

    // ── Post Prices ──────────────────────────────────────

    public void postPrices(RidePriceModel prices, Callback<Void> callback) {
        if (!prices.isValid()) {
            String error = prices.getValidationError();
            Log.e(TAG, "postPrices: Validation failed - " + error);
            callback.onError(400, error);
            return;
        }

        Log.d(TAG, "postPrices: Updating prices on server");
        api.postPrices(prices).enqueue(wrapCallback(callback));
    }

    // ── Internal ─────────────────────────────────────────

    private <T> retrofit2.Callback<T> wrapCallback(Callback<T> callback) {
        return new retrofit2.Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call,
                                   @NonNull Response<T> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Request successful: " + call.request().url());
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = parseErrorMessage(response);
                    Log.e(TAG, "Request failed: " + errorMsg);
                    callback.onError(response.code(), errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call,
                                  @NonNull Throwable t) {
                Log.e(TAG, "Request failed", t);
                callback.onError(-1, "Network error: " + t.getMessage());
            }
        };
    }

    private <T> String parseErrorMessage(Response<T> response) {
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                org.json.JSONObject json = new org.json.JSONObject(errorJson);
                if (json.has("message")) {
                    return json.getString("message");
                }
            }
        } catch (Exception ignored) {}
        return "Request failed (code " + response.code() + ")";
    }
}