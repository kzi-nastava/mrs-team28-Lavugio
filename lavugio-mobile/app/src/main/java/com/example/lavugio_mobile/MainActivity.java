package com.example.lavugio_mobile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
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
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.services.auth.AuthService;
import com.example.lavugio_mobile.ui.GuestHomePageFragment;
import com.example.lavugio_mobile.ui.chat.AdminChatFragment;
import com.example.lavugio_mobile.ui.chat.LiveSupportFragment;
import com.example.lavugio_mobile.ui.profile.ProfileFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private Navbar navbar;
    private FloatingActionButton fabChat;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleOnBackPressed();

        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fine   = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarse = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if (!((fine != null && fine) || (coarse != null && coarse))) {
                        handlePermissionDenied();
                    }
                }
        );

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

        fabChat = findViewById(R.id.fabChat);

        makeChatDraggable();

        // ── Initial fragment ────────────────────────────────────────────────
        if (savedInstanceState == null) {
            if (!AuthService.getInstance().isAuthenticated()) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_container, new GuestHomePageFragment())
                        .commit();
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_container, new ProfileFragment())
                        .commit();
            }
        }

        // ── FAB visibility follows auth state ───────────────────────────────
        AuthService.getInstance().getIsAuthenticated().observe(this, isAuthenticated -> {
            if (isAuthenticated != null && isAuthenticated) {
                fabChat.setVisibility(View.VISIBLE);
            } else {
                fabChat.setVisibility(View.GONE);
            }
        });

        requestLocationPermission();
    }

    // ── Chat ──────────────────────────────────────────────────────────────────

    private void toggleChat() {
        AuthService auth = AuthService.getInstance();
        Fragment chatFragment = getSupportFragmentManager().findFragmentByTag("chat");

        if (chatFragment != null) {
            // Chat je otvoren → ukloni ga
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(chatFragment)
                    .commit();
            getSupportFragmentManager().popBackStack("chat", 1); // izbriši iz backstack-a
        } else {
            // Chat nije otvoren → dodaj novi fragment
            if (auth.isAdmin()) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.content_container, new AdminChatFragment(), "chat")
                        .addToBackStack("chat")
                        .commit();
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.content_container, new LiveSupportFragment(), "chat")
                        .addToBackStack("chat")
                        .commit();
            }
        }
    }

    private void makeChatDraggable() {
        fabChat.setOnTouchListener(new View.OnTouchListener() {

            private float dX, dY;
            private long startClickTime;
            private static final int MAX_CLICK_DURATION = 200; // ms
            private boolean isDragging;
            private float lastX, lastY;

            // Margin u pikselima (pretvori dp u px)
            private final int MARGIN = (int) (16 * getResources().getDisplayMetrics().density);

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {

                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        startClickTime = System.currentTimeMillis();
                        isDragging = false;
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float newX = event.getRawX() + dX;
                        float newY = event.getRawY() + dY;

                        // Clamp da ne izlazi van ekrana + margin
                        int parentWidth = ((View) view.getParent()).getWidth();
                        int parentHeight = ((View) view.getParent()).getHeight();
                        int fabWidth = view.getWidth();
                        int fabHeight = view.getHeight();

                        newX = Math.max(MARGIN, Math.min(newX, parentWidth - fabWidth - MARGIN));
                        newY = Math.max(MARGIN, Math.min(newY, parentHeight - fabHeight - MARGIN));

                        view.setX(newX);
                        view.setY(newY);

                        if (Math.abs(event.getRawX() - lastX) > 5 || Math.abs(event.getRawY() - lastY) > 5) {
                            isDragging = true;
                        }

                        return true;

                    case MotionEvent.ACTION_UP:
                        long clickDuration = System.currentTimeMillis() - startClickTime;

                        if (!isDragging && clickDuration < MAX_CLICK_DURATION) {
                            toggleChat();
                        }

                        // Snap sa marginom
                        int parentW = ((View) view.getParent()).getWidth();
                        float midX = parentW / 2f;
                        if (view.getX() + view.getWidth() / 2f < midX) {
                            view.animate().x(MARGIN).setDuration(150).start(); // snap left + margin
                        } else {
                            view.animate().x(parentW - view.getWidth() - MARGIN).setDuration(150).start(); // snap right + margin
                        }

                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    // ── Permission handling ───────────────────────────────────────────────────

    private void requestLocationPermission() {
        if (hasLocationPermission()) return;
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
        if (auth.isDriver()) {
            Toast.makeText(this,
                    "Location permission is required for drivers. App will close.",
                    Toast.LENGTH_LONG).show();
            new android.os.Handler(android.os.Looper.getMainLooper())
                    .postDelayed(this::finishAffinity, 2000);
        } else {
            Toast.makeText(this,
                    "Location permission denied. Some features may not work.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public boolean isLocationPermissionGranted() {
        return hasLocationPermission();
    }

    // ── Back press ────────────────────────────────────────────────────────────

    private void handleOnBackPressed() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {

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
                Toast.makeText(MainActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                new android.os.Handler(android.os.Looper.getMainLooper())
                        .postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            }
        });
    }
}