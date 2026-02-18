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
import androidx.fragment.app.FragmentManager;

import com.example.lavugio_mobile.MainActivity;
import com.example.lavugio_mobile.R;

public class VerifyEmailFragment extends Fragment {

    private EditText codeInput;
    private Button confirmButton;
    private TextView loginLink;
    private String userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_verify_email, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        codeInput = view.findViewById(R.id.verify_code);
        confirmButton = view.findViewById(R.id.confirm_button);
        loginLink = view.findViewById(R.id.login_link);

        // Get email from arguments
        if (getArguments() != null) {
            userEmail = getArguments().getString("email");
        }

        // Set up confirm button
        if (confirmButton != null) {
            confirmButton.setOnClickListener(v -> handleConfirm());
        }

        // Set up navigation links
        if (loginLink != null) {
            loginLink.setOnClickListener(v -> navigateToLogin());
        }
    }

    private void handleConfirm() {
        String code = codeInput.getText().toString().trim();

        // Validation
        if (code.isEmpty()) {
            Toast.makeText(getContext(), "Please enter the verification code", Toast.LENGTH_SHORT).show();
            return;
        }

        if (code.length() < 4) {
            Toast.makeText(getContext(), "Verification code must be at least 4 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Send verification code to backend
        Toast.makeText(getContext(), "Email verified successfully!", Toast.LENGTH_SHORT).show();

        FragmentManager fm = requireActivity().getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        navigateToLogin();
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
