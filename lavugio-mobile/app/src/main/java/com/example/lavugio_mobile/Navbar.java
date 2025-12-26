package com.example.lavugio_mobile;

import android.animation.ValueAnimator;
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

/**
 * Navbar component for Lavugio Mobile App
 * Provides navigation menu functionality similar to the Angular frontend
 * Menu drops down below the navbar with smooth animations
 */
public class Navbar {
    private LinearLayout navbarContainer;
    private ImageButton menuButton;
    private ViewGroup rootLayout;
    private LinearLayout menuDropdown;
    private AppCompatActivity activity;
    private boolean isMenuOpen = false;
    private boolean isAnimating = false;
    private ValueAnimator currentAnimator = null;

    public Navbar(AppCompatActivity activity, View parentView) {
        this.activity = activity;

        this.navbarContainer = parentView.findViewById(R.id.navbar);
        this.menuButton = parentView.findViewById(R.id.navbar_menu_button);

        // Get the root DecorView to overlay the menu
        this.rootLayout = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);

        initializeMenuButton();
    }

    private void initializeMenuButton() {
        menuButton.setOnClickListener(v -> toggleMenu());
    }

    private void toggleMenu() {
        // Prevent toggling while animation is in progress
        if (isAnimating) {
            return;
        }

        if (isMenuOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    private void openMenu() {
        if (isMenuOpen || isAnimating) return;

        // Cancel any ongoing animation
        if (currentAnimator != null) {
            currentAnimator.cancel();
            currentAnimator = null;
        }

        isAnimating = true;
        isMenuOpen = true;

        // Create and inflate the menu dropdown
        LayoutInflater inflater = LayoutInflater.from(activity);
        menuDropdown = (LinearLayout) inflater.inflate(R.layout.navbar_menu, null);

        // Calculate navbar height to position dropdown below it
        int[] navbarLocation = new int[2];
        navbarContainer.getLocationOnScreen(navbarLocation);
        int navbarBottom = navbarLocation[1] + navbarContainer.getHeight();

        // Create FrameLayout params to position the dropdown
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = navbarBottom;
        menuDropdown.setLayoutParams(params);

        // Set elevation to appear above content
        menuDropdown.setElevation(16f);

        // Add menu dropdown as overlay
        rootLayout.addView(menuDropdown);

        // Measure the dropdown to get its height
        menuDropdown.measure(
                View.MeasureSpec.makeMeasureSpec(rootLayout.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );

        int finalHeight = menuDropdown.getMeasuredHeight();

        // Set initial height to 0 and alpha to 0 for fade-in effect
        menuDropdown.getLayoutParams().height = 0;
        menuDropdown.setAlpha(0f);
        menuDropdown.requestLayout();

        // Animate the dropdown expansion
        currentAnimator = ValueAnimator.ofInt(0, finalHeight);
        currentAnimator.setDuration(300);
        currentAnimator.setInterpolator(new DecelerateInterpolator());
        currentAnimator.addUpdateListener(animation -> {
            if (menuDropdown != null) {
                int height = (int) animation.getAnimatedValue();
                float progress = animation.getAnimatedFraction();

                menuDropdown.getLayoutParams().height = height;
                menuDropdown.setAlpha(progress); // Fade in
                menuDropdown.requestLayout();
            }
        });
        currentAnimator.addListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {}

            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                isAnimating = false;
                setupMenuItemListeners();
                updateMenuButtonIcon();
            }

            @Override
            public void onAnimationCancel(android.animation.Animator animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationRepeat(android.animation.Animator animation) {}
        });
        currentAnimator.start();
    }

    private void closeMenu() {
        if (!isMenuOpen || isAnimating || menuDropdown == null) return;

        // Cancel any ongoing animation
        if (currentAnimator != null) {
            currentAnimator.cancel();
            currentAnimator = null;
        }

        isAnimating = true;
        isMenuOpen = false;

        int currentHeight = menuDropdown.getHeight();

        // Animate the dropdown collapse
        currentAnimator = ValueAnimator.ofInt(currentHeight, 0);
        currentAnimator.setDuration(300);
        currentAnimator.setInterpolator(new DecelerateInterpolator());
        currentAnimator.addUpdateListener(animation -> {
            if (menuDropdown != null) {
                int height = (int) animation.getAnimatedValue();
                float progress = 1f - animation.getAnimatedFraction();

                menuDropdown.getLayoutParams().height = height;
                menuDropdown.setAlpha(progress); // Fade out
                menuDropdown.requestLayout();
            }
        });
        currentAnimator.addListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {}

            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                if (menuDropdown != null && rootLayout != null) {
                    rootLayout.removeView(menuDropdown);
                    menuDropdown = null;
                }
                isAnimating = false;
                updateMenuButtonIcon();
            }

            @Override
            public void onAnimationCancel(android.animation.Animator animation) {
                if (menuDropdown != null && rootLayout != null) {
                    rootLayout.removeView(menuDropdown);
                    menuDropdown = null;
                }
                isAnimating = false;
            }

            @Override
            public void onAnimationRepeat(android.animation.Animator animation) {}
        });
        currentAnimator.start();
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
        // Handle navigation based on selected menu item
        switch (itemName) {
            case "Trips":
                // Navigate to Trips screen
                break;
            case "History":
                Intent intent = new Intent(activity, TripHistoryDriver.class);
                activity.startActivity(intent);
                break;
            case "Reports":
                // Navigate to Reports screen
                break;
            case "Profile":
                // Navigate to Profile screen
                break;
            case "Login":
                // Navigate to Login screen
                break;
            case "Register":
                // Navigate to Register screen
                break;
        }
    }
}