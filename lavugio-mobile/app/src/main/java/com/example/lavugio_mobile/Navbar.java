package com.example.lavugio_mobile;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lavugio_mobile.ui.driver.TripHistoryDriver;

public class Navbar {
    private LinearLayout navbarContainer;
    private ImageButton menuButton;
    private ViewGroup rootLayout;
    private LinearLayout menuDropdown;
    private AppCompatActivity activity;
    private boolean isMenuOpen = false;
    private boolean isAnimating = false;

    public Navbar(AppCompatActivity activity, View parentView) {
        this.activity = activity;
        this.navbarContainer = parentView.findViewById(R.id.navbar);
        this.menuButton = parentView.findViewById(R.id.navbar_menu_button);
        this.rootLayout = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        initializeMenuButton();
    }

    private void initializeMenuButton() {
        menuButton.setOnClickListener(v -> toggleMenu());
    }

    private void toggleMenu() {
        if (isAnimating) return;

        if (isMenuOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    private void openMenu() {
        if (isMenuOpen || isAnimating) return;

        isAnimating = true;
        isMenuOpen = true;

        LayoutInflater inflater = LayoutInflater.from(activity);
        menuDropdown = (LinearLayout) inflater.inflate(R.layout.navbar_menu, null);

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

        // Postavi početne vrednosti za animaciju
        menuDropdown.setPivotY(0);
        menuDropdown.setScaleY(0f);
        menuDropdown.setAlpha(0f);

        rootLayout.addView(menuDropdown);

        // Animiraj sa ViewPropertyAnimator (mnogo brže!)
        menuDropdown.animate()
                .alpha(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    isAnimating = false;
                    setupMenuItemListeners();
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

    private void updateMenuButtonIcon() {
        if (isMenuOpen) {
            menuButton.setImageResource(R.drawable.ic_menu_close);
        } else {
            menuButton.setImageResource(R.drawable.ic_menu_hamburger);
        }
    }

    private void setupMenuItemListeners() {
        if (menuDropdown == null) return;

        TextView tripsItem = menuDropdown.findViewById(R.id.nav_item_trips);
        TextView historyItem = menuDropdown.findViewById(R.id.nav_item_history);
        TextView reportsItem = menuDropdown.findViewById(R.id.nav_item_reports);
        TextView profileItem = menuDropdown.findViewById(R.id.nav_item_profile);
        TextView loginItem = menuDropdown.findViewById(R.id.nav_item_login);
        TextView registerItem = menuDropdown.findViewById(R.id.nav_item_register);

        if (tripsItem != null) {
            tripsItem.setOnClickListener(v -> {
                onMenuItemSelected("Trips");
                closeMenu();
            });
        }

        if (historyItem != null) {
            historyItem.setOnClickListener(v -> {
                onMenuItemSelected("History");
                closeMenu();
            });
        }

        if (reportsItem != null) {
            reportsItem.setOnClickListener(v -> {
                onMenuItemSelected("Reports");
                closeMenu();
            });
        }

        if (profileItem != null) {
            profileItem.setOnClickListener(v -> {
                onMenuItemSelected("Profile");
                closeMenu();
            });
        }

        if (loginItem != null) {
            loginItem.setOnClickListener(v -> {
                onMenuItemSelected("Login");
                closeMenu();
            });
        }

        if (registerItem != null) {
            registerItem.setOnClickListener(v -> {
                onMenuItemSelected("Register");
                closeMenu();
            });
        }
    }

    private void onMenuItemSelected(String itemName) {
        switch (itemName) {
            case "Trips":
                break;
            case "History":
                Intent intent = new Intent(activity, TripHistoryDriver.class);
                activity.startActivity(intent);
                break;
            case "Reports":
                break;
            case "Profile":
                break;
            case "Login":
                break;
            case "Register":
                break;
        }
    }
}