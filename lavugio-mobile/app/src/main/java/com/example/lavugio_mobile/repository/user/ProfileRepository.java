package com.example.lavugio_mobile.repository.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lavugio_mobile.models.Coordinates;
import com.example.lavugio_mobile.models.user.ChangePasswordDTO;
import com.example.lavugio_mobile.models.user.DriverActiveTimeResponse;
import com.example.lavugio_mobile.models.user.DriverEditProfileRequestDTO;
import com.example.lavugio_mobile.models.user.EditProfileDTO;
import com.example.lavugio_mobile.models.user.UserProfileData;
import com.example.lavugio_mobile.services.ApiClient;
import com.example.lavugio_mobile.services.user.UserApi;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileRepository {

    private final UserApi userApi;

    public ProfileRepository() {
        userApi = ApiClient.getInstance().create(UserApi.class);
    }

    public LiveData<UserProfileData> getProfile() {
        MutableLiveData<UserProfileData> data = new MutableLiveData<>();

        userApi.getProfile().enqueue(new Callback<UserProfileData>() {
            @Override
            public void onResponse(Call<UserProfileData> call,
                                   Response<UserProfileData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<UserProfileData> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<byte[]> getProfilePhoto() {
        MutableLiveData<byte[]> data = new MutableLiveData<>();

        userApi.getProfilePhoto().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        data.setValue(response.body().bytes());
                    } catch (Exception e) {
                        data.setValue(null);
                    }
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<Boolean> uploadProfilePhoto(MultipartBody.Part filePart) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        userApi.uploadProfilePhoto(filePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                result.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.setValue(false);
            }
        });

        return result;
    }

    public LiveData<Boolean> updateProfile(EditProfileDTO dto) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        userApi.sendProfileEditRequest(dto)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        result.setValue(response.isSuccessful());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        result.setValue(false);
                    }
                });

        return result;
    }

    public LiveData<Boolean> sendDriverEditRequest(DriverEditProfileRequestDTO dto) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        userApi.sendDriverEditRequest(dto).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                result.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.setValue(false);
            }
        });

        return result;
    }

    public LiveData<Boolean> changePassword(ChangePasswordDTO changePasswordDTO) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        userApi.changePassword(changePasswordDTO).enqueue(new Callback<ResponseBody>() {
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

    public LiveData<String> getDriverActiveLast24Hours() {
        MutableLiveData<String> formattedResult = new MutableLiveData<>();

        userApi.getDriverActiveLast24Hours().enqueue(new Callback<DriverActiveTimeResponse>() {
            @Override
            public void onResponse(Call<DriverActiveTimeResponse> call,
                                   Response<DriverActiveTimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String duration = response.body().getTimeActive();
                    String formatted = formatDuration(duration);
                    formattedResult.setValue(formatted);
                } else {
                    formattedResult.setValue("N/A");
                }
            }

            @Override
            public void onFailure(Call<DriverActiveTimeResponse> call, Throwable t) {
                formattedResult.setValue("N/A");
            }
        });

        return formattedResult;
    }

    public LiveData<Integer> getDriverActiveTotalMinutes() {
        MutableLiveData<Integer> result = new MutableLiveData<>();

        userApi.getDriverActiveLast24Hours().enqueue(new Callback<DriverActiveTimeResponse>() {
            @Override
            public void onResponse(Call<DriverActiveTimeResponse> call,
                                   Response<DriverActiveTimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String duration = response.body().getTimeActive();
                    result.setValue(parseTotalMinutes(duration));
                } else {
                    result.setValue(0);
                }
            }

            @Override
            public void onFailure(Call<DriverActiveTimeResponse> call, Throwable t) {
                result.setValue(0);
            }
        });

        return result;
    }

    public LiveData<Boolean> activateDriver(Coordinates coordinates) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        userApi.activateDriver(coordinates).enqueue(new Callback<ResponseBody>() {
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

    public LiveData<Boolean> deactivateDriver() {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        userApi.deactivateDriver().enqueue(new Callback<ResponseBody>() {
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

    public LiveData<Boolean> getDriverActiveStatus(int driverId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        userApi.getDriverStatus(driverId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        com.google.gson.JsonObject obj =
                                com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                        boolean active = obj.has("active") && obj.get("active").getAsBoolean();
                        result.setValue(active);
                    } catch (Exception e) {
                        result.setValue(false);
                    }
                } else {
                    result.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.setValue(false);
            }
        });

        return result;
    }

    private int parseTotalMinutes(String duration) {
        if (duration == null || duration.isEmpty()) return 0;
        java.util.regex.Pattern pattern =
                java.util.regex.Pattern.compile("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+(?:\\.\\d+)?)S)?");
        java.util.regex.Matcher matcher = pattern.matcher(duration);
        if (!matcher.find()) return 0;
        int hours = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : 0;
        int minutes = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
        return hours * 60 + minutes;
    }

    private String formatDuration(String duration) {
        if (duration == null || duration.isEmpty()) {
            return "0h 0m";
        }

        // ISO 8601 duration format: PT2H30M15S
        java.util.regex.Pattern pattern =
                java.util.regex.Pattern.compile("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+(?:\\.\\d+)?)S)?");
        java.util.regex.Matcher matcher = pattern.matcher(duration);

        if (!matcher.find()) {
            return "0h 0m";
        }

        String hoursStr = matcher.group(1);
        String minutesStr = matcher.group(2);
        String secondsStr = matcher.group(3);

        int hours = hoursStr != null ? Integer.parseInt(hoursStr) : 0;
        int minutes = minutesStr != null ? Integer.parseInt(minutesStr) : 0;
        int seconds = secondsStr != null ? (int) Float.parseFloat(secondsStr) : 0;

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else if (minutes > 0) {
            return minutes + "m " + seconds + "s";
        } else {
            return seconds + "s";
        }
    }
}