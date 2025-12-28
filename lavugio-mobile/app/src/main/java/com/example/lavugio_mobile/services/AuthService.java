package com.example.lavugio_mobile.services;

import android.util.Log;

import com.example.lavugio_mobile.models.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AuthService - Handles all authentication-related API calls
 * TODO: Replace BASE_URL with your actual backend server URL
 */
public class AuthService {

    private static final String TAG = "AuthService";
    // TODO: Update this with your actual backend URL
    private static final String BASE_URL = "https://your-backend-server.com/api";

    /**
     * Login user with email and password
     */
    public static AuthResponse login(String email, String password) {
        try {
            String url = BASE_URL + "/auth/login";
            LoginRequest request = new LoginRequest(email, password);
            
            String response = makePostRequest(url, convertToJson(request));
            return parseAuthResponse(response);
        } catch (Exception e) {
            Log.e(TAG, "Login error: " + e.getMessage());
            return new AuthResponse(false, "Login failed: " + e.getMessage());
        }
    }

    /**
     * Register a new user
     */
    public static AuthResponse register(RegisterRequest request) {
        try {
            String url = BASE_URL + "/auth/register";
            String response = makePostRequest(url, convertToJson(request));
            return parseAuthResponse(response);
        } catch (Exception e) {
            Log.e(TAG, "Registration error: " + e.getMessage());
            return new AuthResponse(false, "Registration failed: " + e.getMessage());
        }
    }

    /**
     * Verify email with code
     */
    public static AuthResponse verifyEmail(String email, String code) {
        try {
            String url = BASE_URL + "/auth/verify-email";
            VerifyEmailRequest request = new VerifyEmailRequest(email, code);
            
            String response = makePostRequest(url, convertToJson(request));
            return parseAuthResponse(response);
        } catch (Exception e) {
            Log.e(TAG, "Email verification error: " + e.getMessage());
            return new AuthResponse(false, "Verification failed: " + e.getMessage());
        }
    }

    /**
     * Request password reset (send email)
     */
    public static AuthResponse requestPasswordReset(String email) {
        try {
            String url = BASE_URL + "/auth/forgot-password";
            PasswordResetRequest request = new PasswordResetRequest(email);
            
            String response = makePostRequest(url, convertToJson(request));
            return parseAuthResponse(response);
        } catch (Exception e) {
            Log.e(TAG, "Password reset request error: " + e.getMessage());
            return new AuthResponse(false, "Password reset request failed: " + e.getMessage());
        }
    }

    /**
     * Reset password with token
     */
    public static AuthResponse resetPassword(String password, String confirmPassword, String token) {
        try {
            String url = BASE_URL + "/auth/reset-password";
            ResetPasswordRequest request = new ResetPasswordRequest(password, confirmPassword, token);
            
            String response = makePostRequest(url, convertToJson(request));
            return parseAuthResponse(response);
        } catch (Exception e) {
            Log.e(TAG, "Reset password error: " + e.getMessage());
            return new AuthResponse(false, "Reset password failed: " + e.getMessage());
        }
    }

    /**
     * Make a POST request
     */
    private static String makePostRequest(String urlString, String jsonData) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // Send JSON data
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read response
            int responseCode = connection.getResponseCode();
            InputStream inputStream = (responseCode == 200 || responseCode == 201) 
                ? connection.getInputStream() 
                : connection.getErrorStream();

            return readInputStream(inputStream);
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Read InputStream to String
     */
    private static String readInputStream(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    /**
     * Convert object to JSON string
     * Note: This is a simple implementation. Consider using a JSON library like Gson or Jackson
     */
    private static String convertToJson(Object obj) {
        // Simple JSON conversion - for production, use Gson or Jackson
        if (obj instanceof LoginRequest) {
            LoginRequest req = (LoginRequest) obj;
            return "{\"email\":\"" + req.getEmail() + "\",\"password\":\"" + req.getPassword() + "\"}";
        } else if (obj instanceof RegisterRequest) {
            RegisterRequest req = (RegisterRequest) obj;
            return "{\"email\":\"" + req.getEmail() + "\",\"password\":\"" + req.getPassword() 
                + "\",\"name\":\"" + req.getName() + "\",\"surname\":\"" + req.getSurname() 
                + "\",\"address\":\"" + req.getAddress() + "\",\"phoneNumber\":\"" + req.getPhoneNumber() + "\"}";
        } else if (obj instanceof VerifyEmailRequest) {
            VerifyEmailRequest req = (VerifyEmailRequest) obj;
            return "{\"email\":\"" + req.getEmail() + "\",\"code\":\"" + req.getCode() + "\"}";
        } else if (obj instanceof PasswordResetRequest) {
            PasswordResetRequest req = (PasswordResetRequest) obj;
            return "{\"email\":\"" + req.getEmail() + "\"}";
        } else if (obj instanceof ResetPasswordRequest) {
            ResetPasswordRequest req = (ResetPasswordRequest) obj;
            return "{\"password\":\"" + req.getPassword() + "\",\"confirmPassword\":\"" + req.getConfirmPassword() 
                + "\",\"token\":\"" + req.getToken() + "\"}";
        }
        return "{}";
    }

    /**
     * Parse JSON response to AuthResponse
     * Note: This is a simple implementation. Consider using Gson or Jackson
     */
    private static AuthResponse parseAuthResponse(String json) {
        // Simple JSON parsing - for production, use Gson or Jackson
        AuthResponse response = new AuthResponse();
        
        try {
            if (json.contains("\"success\":true")) {
                response.setSuccess(true);
            } else {
                response.setSuccess(false);
            }

            // Extract message
            int messageStart = json.indexOf("\"message\":\"");
            if (messageStart != -1) {
                messageStart += 11;
                int messageEnd = json.indexOf("\"", messageStart);
                if (messageEnd != -1) {
                    response.setMessage(json.substring(messageStart, messageEnd));
                }
            }

            // Extract token if present
            int tokenStart = json.indexOf("\"token\":\"");
            if (tokenStart != -1) {
                tokenStart += 9;
                int tokenEnd = json.indexOf("\"", tokenStart);
                if (tokenEnd != -1) {
                    response.setToken(json.substring(tokenStart, tokenEnd));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing response: " + e.getMessage());
            response.setSuccess(false);
            response.setMessage("Error parsing response");
        }

        return response;
    }
}
