package com.example.lavugio_mobile.viewmodel.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lavugio_mobile.models.user.DriverEditProfileRequestDTO;
import com.example.lavugio_mobile.models.user.EditProfileDTO;
import com.example.lavugio_mobile.models.user.UserProfileData;
import com.example.lavugio_mobile.repository.user.ProfileRepository;

import okhttp3.MultipartBody;

public class ProfileViewModel extends ViewModel {

    private final ProfileRepository repository;
    private final MutableLiveData<UserProfileData> profileData = new MutableLiveData<>();
    private final MutableLiveData<byte[]> profilePhotoBytes = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> uploadPhotoSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> driverEditRequestSuccess = new MutableLiveData<>();

    private final MutableLiveData<String> driverActiveTime = new MutableLiveData<>();

    public ProfileViewModel() {
        repository = new ProfileRepository();
    }

    public LiveData<UserProfileData> getProfileData() {
        return profileData;
    }

    public LiveData<byte[]> getProfilePhotoBytes() {
        return profilePhotoBytes;
    }

    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }

    public LiveData<Boolean> getUploadPhotoSuccess() {
        return uploadPhotoSuccess;
    }

    public LiveData<Boolean> getDriverEditRequestSuccess() {
        return driverEditRequestSuccess;
    }

    public LiveData<String> getDriverActiveTime() {
        return driverActiveTime;
    }

    public void loadProfile() {
        repository.getProfile().observeForever(profile -> {
            profileData.setValue(profile);
        });
    }

    public void loadProfilePhoto() {
        repository.getProfilePhoto().observeForever(bytes -> {
            profilePhotoBytes.setValue(bytes);
        });
    }

    public void uploadProfilePhoto(MultipartBody.Part filePart) {
        repository.uploadProfilePhoto(filePart).observeForever(success -> {
            uploadPhotoSuccess.setValue(success);
        });
    }

    public void updateProfile(EditProfileDTO dto) {
        repository.updateProfile(dto).observeForever(success -> {
            updateSuccess.setValue(success);
            if (success) {
                loadProfile();
            }
        });
    }

    public void sendDriverEditRequest(DriverEditProfileRequestDTO dto) {
        repository.sendDriverEditRequest(dto).observeForever(success -> {
            driverEditRequestSuccess.setValue(success);
            if (success) {
                loadProfile();
            }
        });
    }

    public void loadDriverActiveTime() {
        repository.getDriverActiveLast24Hours().observeForever(data -> {
            if (data != null) {
                driverActiveTime.setValue(data);
            }
        });
    }
}