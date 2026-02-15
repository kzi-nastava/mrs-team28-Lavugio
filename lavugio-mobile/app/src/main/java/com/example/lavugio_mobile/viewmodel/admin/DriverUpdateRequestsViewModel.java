package com.example.lavugio_mobile.viewmodel.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lavugio_mobile.models.user.DriverUpdateRequestDiffDTO;
import com.example.lavugio_mobile.repository.admin.DriverUpdateRequestsRepository;

import java.util.List;

import okhttp3.ResponseBody;

public class DriverUpdateRequestsViewModel extends ViewModel {
    private final DriverUpdateRequestsRepository repository;

    private final MutableLiveData<List<DriverUpdateRequestDiffDTO>> requests = new MutableLiveData<>();

    public DriverUpdateRequestsViewModel() {
        repository = new DriverUpdateRequestsRepository();
    }

    public LiveData<List<DriverUpdateRequestDiffDTO>> getDriverUpdateRequests() {
        return requests;
    }

    public void fetchDriverUpdateRequests() {
        repository.getDriverUpdateRequests().observeForever(data -> {
            if (data != null) {
                requests.setValue(data);
            }
        });
    }

    public LiveData<Boolean> approveEditRequest(Long requestId) {
        return repository.approveEditRequest(requestId);
    }

    public LiveData<Boolean> rejectEditRequst(Long requestId) {
        return repository.rejectEditRequest(requestId);
    }
}
