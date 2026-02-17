package com.example.lavugio_mobile.models.auth;

public interface AuthCallback<T> {
    void onSuccess(T result);
    void onError(int code, String message);
}