package com.example.lavugio_mobile.viewmodel.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lavugio_mobile.models.user.DriverRegistrationDTO;
import com.example.lavugio_mobile.models.user.EmailSuggestion;
import com.example.lavugio_mobile.repository.admin.RegisterDriverRepository;

import java.util.List;

public class RegisterDriverViewModel extends ViewModel {
    private final RegisterDriverRepository repository;

    private final MutableLiveData<List<EmailSuggestion>> foundEmail = new MutableLiveData<>();
    public RegisterDriverViewModel() {
        repository = new RegisterDriverRepository();
    }

    public LiveData<String> registerDriver(DriverRegistrationDTO driverRegistrationDTO) {
        return this.repository.registerDriver(driverRegistrationDTO);
    }

    public LiveData<Boolean> fetchSameEmail(String query) {
        return this.repository.getExistingEmail(query);
    }
}
