package com.example.lavugio_mobile.ui.auth;

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
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.MainActivity;
import com.example.lavugio_mobile.R;

public class ResetPasswordFragment extends Fragment {

    private EditText tokenInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private ImageButton passwordToggle;
    private ImageButton confirmPasswordToggle;
    private Button resetButton;
    private TextView registerLink;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private com.example.lavugio_mobile.services.auth.AuthService authService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize AuthService
        authService = com.example.lavugio_mobile.services.auth.AuthService.getInstance();

        // Initialize views
        tokenInput = view.findViewById(R.id.reset_token);
        passwordInput = view.findViewById(R.id.reset_password);
        confirmPasswordInput = view.findViewById(R.id.reset_confirm_password);
        passwordToggle = view.findViewById(R.id.password_toggle);
        confirmPasswordToggle = view.findViewById(R.id.confirm_password_toggle);
        resetButton = view.findViewById(R.id.reset_button);
        registerLink = view.findViewById(R.id.register_link);

        // Set up password toggles
        if (passwordToggle != null) {
            passwordToggle.setOnClickListener(v -> togglePasswordVisibility());
        }
        if (confirmPasswordToggle != null) {
            confirmPasswordToggle.setOnClickListener(v -> toggleConfirmPasswordVisibility());
        }

        // Set up reset button
        if (resetButton != null) {
            resetButton.setOnClickListener(v -> handleResetPassword());
        }

        // Set up navigation links
        if (registerLink != null) {
            registerLink.setOnClickListener(v -> navigateToRegister());
        }
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        passwordInput.setSelection(passwordInput.getText().length());
    }

    private void toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible;

        if (isConfirmPasswordVisible) {
            confirmPasswordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            confirmPasswordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        confirmPasswordInput.setSelection(confirmPasswordInput.getText().length());
    }

    private void handleResetPassword() {
        String token = tokenInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validation
        if (token.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button to prevent double-tap
        resetButton.setEnabled(false);
        resetButton.setText("Resetting...");

        // Send password reset request to backend
        authService.resetPassword(token, password, new com.example.lavugio_mobile.models.auth.AuthCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (!isAdded()) return;

                resetButton.setEnabled(true);
                resetButton.setText("Reset Password");
                Toast.makeText(getContext(), "Password reset successful!", Toast.LENGTH_SHORT).show();

                // Navigate to login
                navigateToLogin();
            }

            @Override
            public void onError(int code, String message) {
                if (!isAdded()) return;

                resetButton.setEnabled(true);
                resetButton.setText("Reset Password");
                Toast.makeText(getContext(), "Failed to reset password: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToRegister() {
        if (getActivity() instanceof MainActivity) {
            Fragment fragment = new RegisterFragment();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToLogin() {
        if (getActivity() instanceof MainActivity) {
            Fragment fragment = new LoginFragment();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
