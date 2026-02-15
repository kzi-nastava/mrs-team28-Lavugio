package com.example.lavugio_mobile.ui.auth;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.MainActivity;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.auth.AuthCallback;
import com.example.lavugio_mobile.services.auth.AuthService;
import com.example.lavugio_mobile.services.WebSocketService;
import com.example.lavugio_mobile.models.auth.LoginRequest;
import com.example.lavugio_mobile.models.auth.LoginResponse;
import com.example.lavugio_mobile.services.DriverService;
import com.example.lavugio_mobile.services.LocationService;

public class LoginFragment extends Fragment {

    private EditText emailInput;
    private EditText passwordInput;
    private ImageButton passwordToggle;
    private Button loginButton;
    private TextView registerLink;
    private TextView forgotPasswordLink;
    private boolean isPasswordVisible = false;

    private AuthService authService;
    private DriverService driverService;
    private LocationService locationService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authService = AuthService.getInstance();
        driverService = new DriverService(new LocationService(requireContext()));
        locationService = new LocationService(requireContext());

        // Initialize views
        emailInput = view.findViewById(R.id.login_email);
        passwordInput = view.findViewById(R.id.login_password);
        passwordToggle = view.findViewById(R.id.login_password_toggle);
        loginButton = view.findViewById(R.id.login_button);
        registerLink = view.findViewById(R.id.register_link);
        forgotPasswordLink = view.findViewById(R.id.forgot_password_link);

        if (emailInput == null || passwordInput == null || passwordToggle == null ||
                loginButton == null || registerLink == null || forgotPasswordLink == null) {
            Toast.makeText(getContext(), "Error: Some views could not be found", Toast.LENGTH_SHORT).show();
            return;
        }

        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());
        loginButton.setOnClickListener(v -> handleLogin());
        registerLink.setOnClickListener(v -> navigateToRegister());
        forgotPasswordLink.setOnClickListener(v -> navigateToForgotPassword());
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordToggle.setImageResource(android.R.drawable.ic_menu_view);
        } else {
            passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordToggle.setImageResource(android.R.drawable.ic_menu_view);
        }

        passwordInput.setSelection(passwordInput.getText().length());
    }

    private void handleLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(getContext(), "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button to prevent double-tap while request is in flight
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        LoginRequest request = new LoginRequest(email, password);

        // Get location permissions if needed
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            // Proceed with login without location for now
            performLogin(request);
            return;
        }

        // Get current location
        locationService.getLocation(new LocationService.LocationCallback() {
            @Override
            public void onLocation(com.example.lavugio_mobile.models.Coordinates coordinates) {
                request.setLatitude(coordinates.getLatitude());
                request.setLongitude(coordinates.getLongitude());
                performLogin(request);
            }

            @Override
            public void onError(String error) {
                // Proceed with login without coordinates
                performLogin(request);
            }
        });
    }
    private void performLogin(LoginRequest request) {
        authService.login(request, new AuthCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse result) {
                if (!isAdded()) return;

                Toast.makeText(getContext(), "Login successful!", Toast.LENGTH_SHORT).show();

                // If driver, activate the driver
                if ("DRIVER".equals(result.getRole())) {
                    driverService.activateDriver(new DriverService.Callback<Object>() {
                        @Override
                        public void onSuccess(Object result) {
                            navigateBasedOnRole("DRIVER");
                        }

                        @Override
                        public void onError(int code, String message) {
                            // Still navigate even if activation fails
                            navigateBasedOnRole("DRIVER");
                        }
                    });
                } else {
                    navigateBasedOnRole(result.getRole());
                }
            }

            @Override
            public void onError(int code, String message) {
                if (!isAdded()) return;

                loginButton.setEnabled(true);
                loginButton.setText("Login");
                Toast.makeText(getContext(), "Login failed: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateBasedOnRole(String role) {
        switch (role) {
            case "DRIVER":
                navigateToDriverScheduledRides();
                break;
            case "ADMINISTRATOR":
                navigateToAdminPanel();
                break;
            case "REGULAR_USER":
            default:
                navigateToFindTrip();
                break;
        }
    }
    private void navigateToRegister() {
        if (getActivity() instanceof MainActivity) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToForgotPassword() {
        if (getActivity() instanceof MainActivity) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, new ForgotPasswordFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToFindTrip() {
        if (getActivity() instanceof MainActivity) {
            // Navigate to find ride fragment (equivalent to find trip)
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container,
                            new com.example.lavugio_mobile.ui.ride.FindRideFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToDriverScheduledRides() {
        if (getActivity() instanceof MainActivity) {
            // For now, navigate to driver trip history (closest equivalent)
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container,
                            new com.example.lavugio_mobile.ui.driver.TripHistoryFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToAdminPanel() {
        if (getActivity() instanceof MainActivity) {
            // Navigate to admin panel fragment
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container,
                            new com.example.lavugio_mobile.ui.admin.AdministratorPanelFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToProfile() {
        if (getActivity() instanceof MainActivity) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container,
                            new com.example.lavugio_mobile.ui.profile.ProfileFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }
}