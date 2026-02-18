package com.example.lavugio_mobile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lavugio_mobile.models.enums.UserRole;
import com.example.lavugio_mobile.services.auth.AuthService;
import com.example.lavugio_mobile.ui.GuestHomePageFragment;
import com.example.lavugio_mobile.ui.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private Navbar navbar;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleOnBackPressed();

        // Register permission launcher before any UI
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fine = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarse = result.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION, false);

                    boolean granted = (fine != null && fine) || (coarse != null && coarse);

                    if (!granted) {
                        handlePermissionDenied();
                    }
                }
        );

        // Fullscreen
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        navbar = new Navbar(this, findViewById(R.id.main));

        if (savedInstanceState == null) {
            if (!AuthService.getInstance().isAuthenticated()){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_container, new GuestHomePageFragment())
                        .commit();
            }
            else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_container, new ProfileFragment())
                        .commit();
            }
        }

        // Request location permission immediately
        requestLocationPermission();
    }

    // ── Permission handling ──────────────────────────────

    private void requestLocationPermission() {
        if (hasLocationPermission()) {
            return; // Already granted
        }

        locationPermissionLauncher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void handlePermissionDenied() {
        AuthService auth = AuthService.getInstance();

        // Check if current user is a driver
        if (auth.isDriver()) {
            Toast.makeText(this,
                    "Location permission is required for drivers. App will close.",
                    Toast.LENGTH_LONG).show();

            // Delay slightly so the user can read the toast
            new android.os.Handler(android.os.Looper.getMainLooper())
                    .postDelayed(this::finishAffinity, 2000);
        } else {
            // Passenger or guest — warn but don't close
            Toast.makeText(this,
                    "Location permission denied. Some features may not work.",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Can be called from anywhere to check if location is available.
     */
    public boolean isLocationPermissionGranted() {
        return hasLocationPermission();
    }

    private void handleOnBackPressed(){
        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {

                    private boolean doubleBackToExitPressedOnce = false;

                    @Override
                    public void handleOnBackPressed() {

                        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            setEnabled(false);
                            getOnBackPressedDispatcher().onBackPressed();
                            setEnabled(true);
                            return;
                        }

                        if (doubleBackToExitPressedOnce) {
                            finish();
                            return;
                        }

                        doubleBackToExitPressedOnce = true;
                        Toast.makeText(MainActivity.this,
                                "Press back again to exit",
                                Toast.LENGTH_SHORT).show();

                        new android.os.Handler(android.os.Looper.getMainLooper())
                                .postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                    }
                });
    }
}