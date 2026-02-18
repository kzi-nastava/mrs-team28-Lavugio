package com.example.lavugio_mobile.repository.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.models.user.BlockUserRequest;
import com.example.lavugio_mobile.models.user.DriverRegistrationDTO;
import com.example.lavugio_mobile.models.user.EmailSuggestion;
import com.example.lavugio_mobile.services.user.AdminApi;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterDriverRepository {
    private AdminApi adminApi;

    public RegisterDriverRepository() {
        adminApi = ApiClient.getInstance().create(AdminApi.class);
    }

    public LiveData<String> registerDriver(DriverRegistrationDTO driverRegistrationDTO) {
        MutableLiveData<String> result = new MutableLiveData<>();
        adminApi.registerDriver(driverRegistrationDTO).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    result.setValue(null);
                } else {
                    String errorMessage = "Failed to register driver.";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            org.json.JSONObject jsonObject = new org.json.JSONObject(errorJson);
                            if (jsonObject.has("message")) {
                                errorMessage = jsonObject.getString("message");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    result.setValue(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.setValue("Network error. Please try again.");
            }
        });
        return result;
    }

    public LiveData<Boolean> getExistingEmail(String email) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        if (email.length() < 2) {
            return result;
        }
        adminApi.getEmailSuggestions(email).enqueue(new Callback<List<EmailSuggestion>>() {
            @Override
            public void onResponse(Call<List<EmailSuggestion>> call, Response<List<EmailSuggestion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean exactMatch = false;
                    for (EmailSuggestion suggestion : response.body()) {
                        if (email.equalsIgnoreCase(suggestion.getEmail())) {
                            exactMatch = true;
                            break;
                        }
                    }
                    result.setValue(exactMatch);
                } else {
                    result.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<List<EmailSuggestion>> call, Throwable t) {
                result.setValue(false);
            }
        });
        return result;
    }
}
