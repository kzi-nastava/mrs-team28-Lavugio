package com.example.lavugio_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.view.WindowManager;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private ImageButton passwordToggle;
    private ImageButton confirmPasswordToggle;
    private Button resetButton;
    private TextView registerLink;
    private ImageButton backButton;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private Navbar navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize navbar
        navbar = new Navbar(this, findViewById(R.id.main));

        // Initialize views
        passwordInput = findViewById(R.id.reset_password);
        confirmPasswordInput = findViewById(R.id.reset_confirm_password);
        passwordToggle = findViewById(R.id.password_toggle);
        confirmPasswordToggle = findViewById(R.id.confirm_password_toggle);
        resetButton = findViewById(R.id.reset_button);
        registerLink = findViewById(R.id.register_link);

        // Set up password toggles
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());
        confirmPasswordToggle.setOnClickListener(v -> toggleConfirmPasswordVisibility());

        // Set up reset button
        resetButton.setOnClickListener(v -> handleResetPassword());

        // Set up navigation links
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(ResetPasswordActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        backButton.setOnClickListener(v -> onBackPressed());
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
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validation
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Send password reset request to backend
        Toast.makeText(this, "Password reset successful!", Toast.LENGTH_SHORT).show();

        // Navigate to login
        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
