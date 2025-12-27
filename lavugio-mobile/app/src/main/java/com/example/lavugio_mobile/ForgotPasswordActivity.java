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

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button sendLinkButton;
    private TextView registerLink;
    private ImageButton backButton;
    private Navbar navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize navbar
        navbar = new Navbar(this, findViewById(R.id.main));

        // Initialize views
        emailInput = findViewById(R.id.forgot_password_email);
        sendLinkButton = findViewById(R.id.send_link_button);
        registerLink = findViewById(R.id.register_link);

        // Set up send link button
        sendLinkButton.setOnClickListener(v -> handleSendLink());

        // Set up navigation links
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void handleSendLink() {
        String email = emailInput.getText().toString().trim();

        // Validation
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Send password reset email to backend
        Toast.makeText(this, "Password reset link sent to your email!", Toast.LENGTH_SHORT).show();

        // Navigate back to login
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
