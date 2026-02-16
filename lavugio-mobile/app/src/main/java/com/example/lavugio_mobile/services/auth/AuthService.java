package com.example.lavugio_mobile.services.auth;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lavugio_mobile.api.AuthApi;
import com.example.lavugio_mobile.models.auth.LoginRequest;
import com.example.lavugio_mobile.models.auth.AuthCallback;
import com.example.lavugio_mobile.models.auth.LoginResponse;
import com.example.lavugio_mobile.models.auth.RegistrationRequest;
import com.example.lavugio_mobile.models.auth.VerifyEmailRequest;
import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.services.WebSocketService;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthService {

    private static volatile AuthService INSTANCE;

    private final AuthApi api;
    private final SessionManager session;
    private final WebSocketService webSocketService;

    private final MutableLiveData<Boolean> isAuthenticated = new MutableLiveData<>();
    private final MutableLiveData<LoginResponse> currentUser = new MutableLiveData<>();

    /**
     * Private constructor — use getInstance() instead.
     */
    private AuthService(Context context, WebSocketService webSocketService) {
        // Use application context to prevent memory leaks
        this.session = new SessionManager(context.getApplicationContext());
        this.webSocketService = webSocketService;

        ApiClient.init(session::getToken);
        this.api = ApiClient.getAuthApi();

        isAuthenticated.setValue(session.hasToken());
        currentUser.setValue(session.getUser());
    }

    /**
     * Initialize the singleton. Call this once in Application.onCreate().
     */
    public static void init(Context context, WebSocketService webSocketService) {
        if (INSTANCE == null) {
            synchronized (AuthService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AuthService(context, webSocketService);
                }
            }
        }
    }

    /**
     * Get the singleton instance. Must call init() first.
     */
    public static AuthService getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException(
                    "AuthService not initialized. Call AuthService.init(context, webSocketService) " +
                            "in your Application.onCreate() first.");
        }
        return INSTANCE;
    }

    // ── Observables ──────────────────────────────────────

    public LiveData<Boolean> getIsAuthenticated() {
        return isAuthenticated;
    }

    public LiveData<LoginResponse> getCurrentUser() {
        return currentUser;
    }

    // ── Register (no profile picture) ────────────────────

    public void register(RegistrationRequest request, AuthCallback<Void> callback) {
        api.register(
                toRequestBody(request.getEmail()),
                toRequestBody(request.getPassword()),
                toRequestBody(request.getName()),
                toRequestBody(request.getLastName()),
                toRequestBody(request.getPhoneNumber()),
                toRequestBody(request.getAddress())
        ).enqueue(wrapCallback(callback));
    }

    // ── Register (with profile picture) ──────────────────

    public void registerWithFile(RegistrationRequest request, File imageFile,
                                 AuthCallback<Void> callback) {
        String mimeType = getMimeTypeFromFile(imageFile);
        RequestBody fileBody = RequestBody.create(
                MediaType.parse(mimeType), imageFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "profilePicture", imageFile.getName(), fileBody);

        api.registerWithFile(
                toRequestBody(request.getEmail()),
                toRequestBody(request.getPassword()),
                toRequestBody(request.getName()),
                toRequestBody(request.getLastName()),
                toRequestBody(request.getPhoneNumber()),
                toRequestBody(request.getAddress()),
                filePart
        ).enqueue(wrapCallback(callback));
    }

    // ── Login ────────────────────────────────────────────

    public void login(LoginRequest request, AuthCallback<LoginResponse> callback) {
        api.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse body = response.body();
                    storeToken(body.getToken(), body);
                    callback.onSuccess(body);
                } else {
                    callback.onError(response.code(), parseErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onError(-1, t.getMessage());
            }
        });
    }

    // ── Verify Email ─────────────────────────────────────

    public void verifyEmail(String token, AuthCallback<Void> callback) {
        api.verifyEmail(new VerifyEmailRequest(token)).enqueue(wrapCallback(callback));
    }

    // ── Forgot Password ───────────────────────────────────

    public void forgotPassword(String email, AuthCallback<Void> callback) {
        java.util.Map<String, String> request = java.util.Map.of("email", email);
        api.forgotPassword(request).enqueue(wrapCallback(callback));
    }

    // ── Reset Password ────────────────────────────────────

    public void resetPassword(String token, String newPassword, AuthCallback<Void> callback) {
        java.util.Map<String, String> request = java.util.Map.of(
            "token", token,
            "newPassword", newPassword
        );
        api.resetPassword(request).enqueue(wrapCallback(callback));
    }

    // ── Logout ───────────────────────────────────────────

    public void logout(AuthCallback<Void> callback) {
        Integer userId = session.getUserId();
        if (userId != null) {
            api.logout(userId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    clearAuthData();
                    callback.onSuccess(null);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    clearAuthData();
                    callback.onError(-1, t.getMessage());
                }
            });
        } else {
            clearAuthData();
            callback.onSuccess(null);
        }
    }

    // ── Token helpers ────────────────────────────────────

    public void storeToken(String token, LoginResponse user) {
        session.saveToken(token);
        session.saveUser(user);
        isAuthenticated.postValue(true);
        currentUser.postValue(user);
    }

    public String getToken() {
        return session.getToken();
    }

    public LoginResponse getStoredUser() {
        return session.getUser();
    }

    public Integer getUserId() {
        return session.getUserId();
    }

    public boolean isAuthenticated() {
        return session.hasToken();
    }

    // ── Role helpers ─────────────────────────────────────

    public String getUserRole() {
        return session.getUserRole();
    }

    public boolean isDriver() {
        return "DRIVER".equals(getUserRole());
    }

    public boolean isRegularUser() {
        return "REGULAR_USER".equals(getUserRole());
    }

    public boolean isAdmin() {
        return "ADMIN".equals(getUserRole());
    }

    // ── Internal ─────────────────────────────────────────

    private void clearAuthData() {
        webSocketService.disconnect();
        session.clear();
        isAuthenticated.postValue(false);
        currentUser.postValue(null);
    }

    private RequestBody toRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
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
        } catch (Exception ignored) {
        }
        return "Request failed (code " + response.code() + ")";
    }

    private <T> Callback<T> wrapCallback(AuthCallback<T> callback) {
        return new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(response.code(), parseErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callback.onError(-1, t.getMessage());
            }
        };
    }

    private String getMimeTypeFromFile(File file) {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".webp")) {
            return "image/webp";
        } else if (fileName.endsWith(".heic")) {
            return "image/heic";
        } else if (fileName.endsWith(".heif")) {
            return "image/heif";
        } else {
            return "image/*"; // fallback
        }
    }
}