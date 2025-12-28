package com.example.lavugio_mobile.ui.auth;

import android.os.Bundle;
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

public class ForgotPasswordFragment extends Fragment {

    private EditText emailInput;
    private Button sendLinkButton;
    private TextView registerLink;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        emailInput = view.findViewById(R.id.forgot_password_email);
        sendLinkButton = view.findViewById(R.id.send_link_button);
        registerLink = view.findViewById(R.id.register_link);

        // Set up send link button
        if (sendLinkButton != null) {
            sendLinkButton.setOnClickListener(v -> handleSendLink());
        }

        // Set up navigation links
        if (registerLink != null) {
            registerLink.setOnClickListener(v -> navigateToRegister());
        }
    }

    private void handleSendLink() {
        String email = emailInput.getText().toString().trim();

        // Validation
        if (email.isEmpty()) {
            Toast.makeText(getContext(), "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Send password reset email to backend
        Toast.makeText(getContext(), "Password reset link sent to your email!", Toast.LENGTH_SHORT).show();

        // Navigate back to login
        navigateToLogin();
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
