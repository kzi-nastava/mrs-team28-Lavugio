package com.example.lavugio_mobile.viewmodel.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lavugio_mobile.models.user.ChangePasswordDTO;
import com.example.lavugio_mobile.repository.user.ProfileRepository;

public class ChangePasswordViewModel extends ViewModel {

    private final ProfileRepository repository;
    private final MutableLiveData<Boolean> passwordChangeResult = new MutableLiveData<>();

    public ChangePasswordViewModel() {
        repository = new ProfileRepository();
    }

    public LiveData<Boolean> getPasswordChangeResult() {
        return passwordChangeResult;
    }

    public void changePassword(String oldPassword, String newPassword) {
        ChangePasswordDTO dto = new ChangePasswordDTO(oldPassword, newPassword);

        repository.changePassword(dto).observeForever(result -> {
            passwordChangeResult.setValue(result);
        });
    }
}