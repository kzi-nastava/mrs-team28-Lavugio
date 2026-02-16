package com.example.lavugio_mobile.viewmodel.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lavugio_mobile.models.user.EmailSuggestion;
import com.example.lavugio_mobile.repository.admin.BlockUserRepository;

import java.util.List;

public class BlockUserViewModel extends ViewModel {
    private final BlockUserRepository repository;

    private final MutableLiveData<List<EmailSuggestion>> suggestions = new MutableLiveData<>();

    public BlockUserViewModel() {
        repository = new BlockUserRepository();
    }

    public LiveData<Boolean> blockUser(String email, String reason) {
        return repository.blockUser(email, reason);
    }

    public LiveData<List<EmailSuggestion>> getSuggestions() {
        return suggestions;
    }

    public void fetchEmailSuggestions(String query) {
        repository.getEmailSuggestions(query).observeForever(data -> {
            if (data != null) {
                suggestions.setValue(data);
            }
        });
    }
}
