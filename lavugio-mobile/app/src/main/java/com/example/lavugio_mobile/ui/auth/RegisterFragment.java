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

public class RegisterFragment extends Fragment {

    private EditText emailInput;
    private EditText passwordInput;
    private EditText nameInput;
    private EditText surnameInput;
    private EditText addressInput;
    private EditText phoneInput;
    private ImageButton passwordToggle;
    private Button registerButton;
    private TextView loginLink;
    private boolean isPasswordVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        emailInput = view.findViewById(R.id.register_email);
        passwordInput = view.findViewById(R.id.register_password);
        nameInput = view.findViewById(R.id.register_name);
        surnameInput = view.findViewById(R.id.register_surname);
        addressInput = view.findViewById(R.id.register_address);
        phoneInput = view.findViewById(R.id.register_phone);
        passwordToggle = view.findViewById(R.id.register_password_toggle);
        registerButton = view.findViewById(R.id.register_button);
        loginLink = view.findViewById(R.id.login_link);

        // Verify all views are found
        if (emailInput == null || passwordInput == null || nameInput == null ||
                surnameInput == null || addressInput == null || phoneInput == null ||
                passwordToggle == null || registerButton == null || loginLink == null) {
            Toast.makeText(getContext(), "Error: Some views could not be found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set up password toggle
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());

        // Set up register button
        registerButton.setOnClickListener(v -> handleRegister());

        // Set up navigation links
        loginLink.setOnClickListener(v -> navigateToLogin());
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

    private void handleRegister() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        String surname = surnameInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        // Validation
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() ||
                surname.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            Toast.makeText(getContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Send registration request to backend
        Toast.makeText(getContext(), "Registration successful! Please verify your email.", Toast.LENGTH_SHORT).show();

        // Navigate to verify email
        try {
            VerifyEmailFragment verifyEmailFragment = new VerifyEmailFragment();
            Bundle args = new Bundle();
            args.putString("email", email);
            verifyEmailFragment.setArguments(args);

            if (getActivity() instanceof MainActivity) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_container, verifyEmailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error navigating: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
