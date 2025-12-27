package com.example.lavugio_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VerifyEmailActivity extends AppCompatActivity {

    private EditText codeInput;
    private Button confirmButton;
    private TextView loginLink;
    private ImageButton backButton;
    private String userEmail;
    private Navbar navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_email);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize navbar
        navbar = new Navbar(this, findViewById(R.id.main));

        // Initialize views
        codeInput = findViewById(R.id.verify_code);
        confirmButton = findViewById(R.id.confirm_button);
        loginLink = findViewById(R.id.login_link);

        // Get email from intent
        userEmail = getIntent().getStringExtra("email");

        // Set up confirm button
        confirmButton.setOnClickListener(v -> handleConfirm());

        // Set up navigation links
        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(VerifyEmailActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void handleConfirm() {
        String code = codeInput.getText().toString().trim();

        // Validation
        if (code.isEmpty()) {
            Toast.makeText(this, "Please enter the verification code", Toast.LENGTH_SHORT).show();
            return;
        }

        if (code.length() < 4) {
            Toast.makeText(this, "Verification code must be at least 4 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Send verification code to backend
        Toast.makeText(this, "Email verified successfully!", Toast.LENGTH_SHORT).show();

        // Navigate to login
        Intent intent = new Intent(VerifyEmailActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
