package com.example.lavugio_mobile.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lavugio_mobile.R;

public class AdministratorPanelActivity extends AppCompatActivity {
    private Button btnRegisterDriver;
    private Button btnDriverUpdateRequests;
    private Button btnBlockUser;
    private Button btnSeeReports;
    private Button btnPanicAlerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_admin_panel);

        // Initialize buttons
        btnRegisterDriver = findViewById(R.id.btnRegisterDriver);
        btnDriverUpdateRequests = findViewById(R.id.btnDriverUpdateRequests);
        btnBlockUser = findViewById(R.id.btnBlockUser);
        btnSeeReports = findViewById(R.id.btnSeeReports);
        btnPanicAlerts = findViewById(R.id.btnPanicAlerts);

        // Set click listeners
        btnRegisterDriver.setOnClickListener(v -> {
            //Intent intent = new Intent(AdministratorPanelActivity.this, RegisterDriverActivity.class);
            //startActivity(intent);
        });

        btnDriverUpdateRequests.setOnClickListener(v -> {
            //Intent intent = new Intent(AdministratorPanelActivity.this, DriverUpdateRequestsActivity.class);
            //startActivity(intent);
        });

        btnBlockUser.setOnClickListener(v -> {
            Intent intent = new Intent(AdministratorPanelActivity.this, BlockUserActivity.class);
            startActivity(intent);
        });

        btnSeeReports.setOnClickListener(v -> {
            //Intent intent = new Intent(AdministratorPanelActivity.this, SeeReportsActivity.class);
            //startActivity(intent);
        });

        btnPanicAlerts.setOnClickListener(v -> {
            //Intent intent = new Intent(AdministratorPanelActivity.this, PanicAlertsActivity.class);
            //startActivity(intent);
        });
    }
}
