package com.example.lavugio_mobile;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.example.lavugio_mobile.models.auth.AuthCallback;
import com.example.lavugio_mobile.services.auth.AuthService;
import com.example.lavugio_mobile.models.auth.LoginResponse;
import com.example.lavugio_mobile.ui.admin.AdministratorPanelFragment;
import com.example.lavugio_mobile.ui.auth.LoginFragment;
import com.example.lavugio_mobile.ui.auth.RegisterFragment;
import com.example.lavugio_mobile.ui.driver.TripHistoryFragment;
import com.example.lavugio_mobile.ui.profile.ProfileFragment;
import com.example.lavugio_mobile.ui.reports.RidesReportsFragment;
import com.example.lavugio_mobile.ui.ride.FindRideFragment;

public class Navbar {

    private final LinearLayout navbarContainer;
    private final ImageButton menuButton;
    private final ViewGroup rootLayout;
    private final TextView logoView;
    private final AppCompatActivity activity;
    private final AuthService authService;

    private LinearLayout menuDropdown;
    private boolean isMenuOpen = false;
    private boolean isAnimating = false;

    // ── Observable state ─────────────────────────────────
    // These are LiveData so the menu auto-refreshes when values change.

    private final MutableLiveData<Boolean> driverActive = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> statusLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> hasNewNotifications = new MutableLiveData<>(false);
    private final MutableLiveData<Long> latestRideId = new MutableLiveData<>(null);

    // Track the last known role so we can detect role changes
    private String lastKnownRole = null;

    public Navbar(AppCompatActivity activity, View parentView) {
        this.activity = activity;
        this.authService = AuthService.getInstance();
        this.navbarContainer = parentView.findViewById(R.id.navbar);
        this.menuButton = parentView.findViewById(R.id.navbar_menu_button);
        this.rootLayout = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        this.logoView = parentView.findViewById(R.id.navbar_logo);

        initializeMenuButton();
        initializeLogoButton();
        observeAuthState();
        observeCurrentUser();
        observeDynamicState();
    }

    // ── Public setters ───────────────────────────────────

    public void setLatestRideId(Long rideId) {
        this.latestRideId.setValue(rideId);
    }

    public void setHasNewNotifications(boolean hasNew) {
        this.hasNewNotifications.setValue(hasNew);
    }

    public void setDriverActive(boolean active) {
        this.driverActive.setValue(active);
    }

    // ── Auth & user observers ────────────────────────────

    private void observeAuthState() {
        authService.getIsAuthenticated().observe(activity, isLoggedIn -> {
            if (isLoggedIn != null && isLoggedIn) {
                navbarContainer.setVisibility(View.VISIBLE);
            } else {
                navbarContainer.setVisibility(View.GONE);
                // Force close and reset when logged out
                if (isMenuOpen) {
                    forceCloseMenu();
                }
                // Reset all state
                lastKnownRole = null;
                driverActive.setValue(false);
                statusLoading.setValue(false);
                hasNewNotifications.setValue(false);
                latestRideId.setValue(null);
            }
        });
    }

    /**
     * Observe the current user so we detect role changes
     * (e.g. logging out as DRIVER and back in as REGULAR_USER).
     * If the role changes while the menu is open, close and reopen it
     * with the correct layout.
     */
    private void observeCurrentUser() {
        authService.getCurrentUser().observe(activity, user -> {
            String newRole = (user != null) ? user.getRole() : null;

            if (newRole != null && !newRole.equals(lastKnownRole)) {
                lastKnownRole = newRole;

                // If the menu is currently open, rebuild it with the new role's layout
                if (isMenuOpen) {
                    forceCloseMenu();
                    // Small delay so the close finishes before reopening
                    new android.os.Handler(android.os.Looper.getMainLooper())
                            .postDelayed(this::openMenu, 50);
                }
            }
        });
    }

    /**
     * Observe dynamic state changes and refresh the open menu in real time.
     */
    private void observeDynamicState() {
        driverActive.observe(activity, active -> {
            if (isMenuOpen) refreshDriverStatusButton();
        });

        statusLoading.observe(activity, loading -> {
            if (isMenuOpen) refreshDriverStatusButton();
        });

        hasNewNotifications.observe(activity, hasNew -> {
            if (isMenuOpen) refreshNotificationItem();
        });

        latestRideId.observe(activity, rideId -> {
            if (isMenuOpen) refreshLatestRideItem();
        });
    }

    // ── Initialization ───────────────────────────────────

    private void initializeMenuButton() {
        menuButton.setOnClickListener(v -> toggleMenu());
    }

    private void initializeLogoButton() {
        if (logoView != null) {
            logoView.setOnClickListener(v -> {
                closeMenu();
                navigateToHome();
            });
        }
    }

    // ── Menu open / close ────────────────────────────────

    private void toggleMenu() {
        if (isAnimating) return;
        if (isMenuOpen) closeMenu();
        else openMenu();
    }

    private void openMenu() {
        if (isMenuOpen || isAnimating) return;

        isAnimating = true;
        isMenuOpen = true;

        int menuLayoutRes = getMenuLayoutForCurrentState();

        LayoutInflater inflater = LayoutInflater.from(activity);
        menuDropdown = (LinearLayout) inflater.inflate(menuLayoutRes, null);

        int[] navbarLocation = new int[2];
        navbarContainer.getLocationOnScreen(navbarLocation);
        int navbarBottom = navbarLocation[1] + navbarContainer.getHeight();

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = navbarBottom;
        menuDropdown.setLayoutParams(params);
        menuDropdown.setElevation(16f);

        menuDropdown.setPivotY(0);
        menuDropdown.setScaleY(0f);
        menuDropdown.setAlpha(0f);

        rootLayout.addView(menuDropdown);

        menuDropdown.animate()
                .alpha(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    isAnimating = false;
                    setupMenuItemListeners();
                    applyDynamicState();
                    updateMenuButtonIcon();
                })
                .start();
    }

    private void closeMenu() {
        if (!isMenuOpen || isAnimating || menuDropdown == null) return;

        isAnimating = true;
        isMenuOpen = false;

        menuDropdown.animate()
                .alpha(0f)
                .scaleY(0f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    if (menuDropdown != null && rootLayout != null) {
                        rootLayout.removeView(menuDropdown);
                        menuDropdown = null;
                    }
                    isAnimating = false;
                    updateMenuButtonIcon();
                })
                .start();
    }

    private void forceCloseMenu() {
        isMenuOpen = false;
        isAnimating = false;
        if (menuDropdown != null && rootLayout != null) {
            rootLayout.removeView(menuDropdown);
            menuDropdown = null;
        }
        updateMenuButtonIcon();
    }

    private void updateMenuButtonIcon() {
        if (isMenuOpen) {
            menuButton.setImageResource(R.drawable.ic_menu_close);
        } else {
            menuButton.setImageResource(R.drawable.ic_menu_hamburger);
        }
    }

    // ── Menu layout selection ────────────────────────────

    private int getMenuLayoutForCurrentState() {
        if (!authService.isAuthenticated()) {
            return R.layout.navbar_menu_guest;
        }

        String role = authService.getUserRole();
        if (role == null) return R.layout.navbar_menu_guest;

        switch (role) {
            case "DRIVER":
                return R.layout.navbar_menu_driver;
            case "ADMIN":
                return R.layout.navbar_menu_admin;
            case "REGULAR_USER":
            default:
                return R.layout.navbar_menu_regular_user;
        }
    }

    // ── Apply / refresh dynamic state ────────────────────

    private void applyDynamicState() {
        refreshLatestRideItem();
        refreshDriverStatusButton();
        refreshNotificationItem();
    }

    private void refreshLatestRideItem() {
        if (menuDropdown == null) return;
        TextView latestRideItem = menuDropdown.findViewById(R.id.nav_item_latest_ride);
        if (latestRideItem != null) {
            Long rideId = latestRideId.getValue();
            latestRideItem.setVisibility(rideId != null ? View.VISIBLE : View.GONE);
        }
    }

    private void refreshDriverStatusButton() {
        if (menuDropdown == null) return;
        Button statusButton = menuDropdown.findViewById(R.id.nav_driver_status_button);
        if (statusButton == null) return;

        Boolean loading = statusLoading.getValue();
        Boolean active = driverActive.getValue();

        if (Boolean.TRUE.equals(loading)) {
            statusButton.setText("Updating...");
            statusButton.setEnabled(false);
            statusButton.setAlpha(0.5f);
        } else if (Boolean.TRUE.equals(active)) {
            statusButton.setText("Online");
            statusButton.setEnabled(true);
            statusButton.setAlpha(1f);
            statusButton.setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#16A34A")));
        } else {
            statusButton.setText("Offline");
            statusButton.setEnabled(true);
            statusButton.setAlpha(1f);
            statusButton.setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#DC2626")));
        }
    }

    private void refreshNotificationItem() {
        if (menuDropdown == null) return;
        TextView notifItem = menuDropdown.findViewById(R.id.nav_item_notifications);
        if (notifItem == null) return;

        if (Boolean.TRUE.equals(hasNewNotifications.getValue())) {
            notifItem.setTextColor(Color.parseColor("#EF4444"));
            notifItem.setTypeface(null, Typeface.BOLD);
            notifItem.setText("Notifications ●");
        } else {
            notifItem.setTextColor(Color.parseColor("#FEFAE0"));
            notifItem.setTypeface(null, Typeface.NORMAL);
            notifItem.setText("Notifications");
        }
    }

    // ── Menu item click listeners ────────────────────────

    private void setupMenuItemListeners() {
        if (menuDropdown == null) return;

        if (!authService.isAuthenticated()) {
            setMenuClickListener(R.id.nav_item_login,
                    () -> navigateToFragment(new LoginFragment()));
            setMenuClickListener(R.id.nav_item_register,
                    () -> navigateToFragment(new RegisterFragment()));
            return;
        }

        String role = authService.getUserRole();

        // Common to all authenticated roles
        setMenuClickListener(R.id.nav_item_profile,
                () -> navigateToFragment(new ProfileFragment()));
        setMenuClickListener(R.id.nav_item_reports,
                this::onReportsClicked);
        setMenuClickListener(R.id.nav_item_notifications,
                this::onNotificationsClicked);
        setMenuClickListener(R.id.nav_item_logout,
                this::handleLogout);

        // Regular User
        if ("REGULAR_USER".equals(role)) {
            setMenuClickListener(R.id.nav_item_latest_ride, () -> {
                Long rideId = latestRideId.getValue();
                if (rideId != null) onLatestRideClicked(rideId);
            });
            setMenuClickListener(R.id.nav_item_order_ride,
                    () -> navigateToFragment(new FindRideFragment()));
            setMenuClickListener(R.id.nav_item_rides,
                    this::onRidesClicked);
            setMenuClickListener(R.id.nav_item_history,
                    () -> navigateToFragment(new TripHistoryFragment()));
        }

        // Driver
        if ("DRIVER".equals(role)) {
            Button statusButton = menuDropdown.findViewById(R.id.nav_driver_status_button);
            if (statusButton != null) {
                statusButton.setOnClickListener(v -> toggleDriverStatus());
            }
            setMenuClickListener(R.id.nav_item_history,
                    () -> navigateToFragment(new TripHistoryFragment()));
            setMenuClickListener(R.id.nav_item_rides,
                    this::onDriverRidesClicked);
        }

        // Admin
        if ("ADMIN".equals(role)) {
            setMenuClickListener(R.id.nav_item_admin_panel,
                    () -> navigateToFragment(new AdministratorPanelFragment()));
        }
    }

    private void setMenuClickListener(int viewId, Runnable action) {
        if (menuDropdown == null) return;
        View item = menuDropdown.findViewById(viewId);
        if (item != null) {
            item.setOnClickListener(v -> {
                action.run();
                closeMenu();
            });
        }
    }

    // ── Actions ──────────────────────────────────────────

    private void toggleDriverStatus() {
        statusLoading.setValue(true);

        // TODO: Replace with actual API call to toggle driver status
        new android.os.Handler(android.os.Looper.getMainLooper())
                .postDelayed(() -> {
                    boolean current = Boolean.TRUE.equals(driverActive.getValue());
                    driverActive.setValue(!current);
                    statusLoading.setValue(false);

                    String status = Boolean.TRUE.equals(driverActive.getValue()) ? "Online" : "Offline";
                    Toast.makeText(activity, "Status: " + status, Toast.LENGTH_SHORT).show();
                }, 500);
    }

    private void handleLogout() {
        authService.logout(new AuthCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(activity, "Logged out", Toast.LENGTH_SHORT).show();
                navigateToFragment(new LoginFragment());
            }

            @Override
            public void onError(int code, String message) {
                Toast.makeText(activity, "Logout failed: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onMenuItemSelected(String itemName) {
        switch (itemName) {
            case "Trips":
                navigateToFragment(new FindRideFragment());
                break;
            case "History":
                navigateToFragment(new TripHistoryFragment());
                break;
            case "Reports":
                navigateToFragment(new RidesReportsFragment());
                break;
            case "Profile":
                // Navigate to Profile screen
                navigateToFragment(new ProfileFragment());
                break;
            case "Login":
                // Navigate to Login screen
                navigateToFragment(new LoginFragment());
        }
    }

    private void navigateToHome() {
        String role = authService.getUserRole();
        if (role == null) return;

        switch (role) {
            case "DRIVER":
                // TODO: navigate to driver home
                break;
            case "ADMIN":
                navigateToFragment(new AdministratorPanelFragment());
                break;
            case "REGULAR_USER":
            default:
                navigateToFragment(new FindRideFragment());
                break;
        }
    }

    private void onLatestRideClicked(long rideId) {
        // TODO: Navigate to ride overview fragment
        Toast.makeText(activity, "Latest Ride #" + rideId, Toast.LENGTH_SHORT).show();
    }

    private void onRidesClicked() {
        // TODO: Navigate to active rides fragment
        Toast.makeText(activity, "Active Rides — coming soon", Toast.LENGTH_SHORT).show();
    }

    private void onDriverRidesClicked() {
        // TODO: Navigate to driver scheduled rides fragment
        Toast.makeText(activity, "Scheduled Rides — coming soon", Toast.LENGTH_SHORT).show();
    }

    private void onReportsClicked() {
        // TODO: Navigate to reports fragment
        Toast.makeText(activity, "Reports — coming soon", Toast.LENGTH_SHORT).show();
    }

    private void onNotificationsClicked() {
        // TODO: Navigate to notifications fragment
        Toast.makeText(activity, "Notifications — coming soon", Toast.LENGTH_SHORT).show();
    }

    // ── Navigation helper ────────────────────────────────

    private void navigateToFragment(Fragment fragment) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}