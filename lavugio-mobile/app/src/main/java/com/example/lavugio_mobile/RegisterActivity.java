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

public class RegisterActivity extends AppCompatActivity {

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
    private Navbar navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize navbar
        navbar = new Navbar(this, findViewById(R.id.main));

        // Initialize views
        emailInput = findViewById(R.id.register_email);
        passwordInput = findViewById(R.id.register_password);
        nameInput = findViewById(R.id.register_name);
        surnameInput = findViewById(R.id.register_surname);
        addressInput = findViewById(R.id.register_address);
        phoneInput = findViewById(R.id.register_phone);
        passwordToggle = findViewById(R.id.register_password_toggle);
        registerButton = findViewById(R.id.register_button);
        loginLink = findViewById(R.id.login_link);

        // Set up password toggle
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());

        // Set up register button
        registerButton.setOnClickListener(v -> handleRegister());

        // Set up navigation links
        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
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
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Send registration request to backend
        Toast.makeText(this, "Registration successful! Please verify your email.", Toast.LENGTH_SHORT).show();

        // Navigate to verify email
        Intent intent = new Intent(RegisterActivity.this, VerifyEmailActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }
}