package com.example.lavugio_mobile.repository.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.models.user.BlockUserRequest;
import com.example.lavugio_mobile.models.user.DriverUpdateRequestDiffDTO;
import com.example.lavugio_mobile.models.user.EmailSuggestion;
import com.example.lavugio_mobile.services.user.AdminApi;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockUserRepository {
    private final AdminApi adminApi;

    public BlockUserRepository() {
        adminApi = ApiClient.getInstance().create(AdminApi.class);
    }

    public LiveData<Boolean> blockUser(String email, String reason) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        BlockUserRequest blockUserRequest = new BlockUserRequest(email, reason);
        adminApi.blockUser(blockUserRequest).enqueue(new Callback<ResponseBody>() {
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

    public LiveData<List<EmailSuggestion>> getEmailSuggestions(String email) {
        MutableLiveData<List<EmailSuggestion>> result = new MutableLiveData<>();
        if (email.length() < 2) {
            return result;
        }
        adminApi.getEmailSuggestions(email).enqueue(new Callback<List<EmailSuggestion>>() {
            @Override
            public void onResponse(Call<List<EmailSuggestion>> call, Response<List<EmailSuggestion>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<EmailSuggestion>> call, Throwable t) {
            }
        });
        return result;
    }
}
