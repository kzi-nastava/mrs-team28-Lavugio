package com.example.lavugio_mobile;

import android.animation.ValueAnimator;
import android.content.Context;
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
    private FrameLayout contentContainer;
    private LinearLayout menuDropdown;
    private AppCompatActivity activity;
    private boolean isMenuOpen = false;

    public Navbar(AppCompatActivity activity, View parentView) {
        this.activity = activity;
        this.navbarContainer = parentView.findViewById(R.id.navbar);
        this.menuButton = parentView.findViewById(R.id.navbar_menu_button);
        this.contentContainer = parentView.findViewById(R.id.content_container);
        
        initializeMenuButton();
    }

    private void initializeMenuButton() {
        menuButton.setOnClickListener(v -> toggleMenu());
    }

    private void toggleMenu() {
        if (isMenuOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    private void openMenu() {
        if (isMenuOpen) return;
        
        isMenuOpen = true;
        
        // Create and inflate the menu dropdown
        LayoutInflater inflater = LayoutInflater.from(activity);
        menuDropdown = (LinearLayout) inflater.inflate(R.layout.navbar_menu, null);
        
        // Add menu to the parent container above the content
        ViewGroup parentLayout = (ViewGroup) contentContainer.getParent();
        parentLayout.addView(menuDropdown, 1);
        
        // Set initial height to 0
        menuDropdown.measure(
                View.MeasureSpec.makeMeasureSpec(parentLayout.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        
        int finalHeight = menuDropdown.getMeasuredHeight();
        menuDropdown.getLayoutParams().height = 0;
        menuDropdown.requestLayout();
        
        // Animate the dropdown expansion
        ValueAnimator animator = ValueAnimator.ofInt(0, finalHeight);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int height = (int) animation.getAnimatedValue();
            menuDropdown.getLayoutParams().height = height;
            menuDropdown.requestLayout();
        });
        animator.start();
        
        // Update menu button icon
        updateMenuButtonIcon();
        
        // Setup menu item click listeners
        setupMenuItemListeners();
    }

    private void closeMenu() {
        if (!isMenuOpen || menuDropdown == null) return;
        
        isMenuOpen = false;
        
        // Animate the dropdown collapse
        ValueAnimator animator = ValueAnimator.ofInt(menuDropdown.getHeight(), 0);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int height = (int) animation.getAnimatedValue();
            menuDropdown.getLayoutParams().height = height;
            menuDropdown.requestLayout();
        });
        animator.addListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {}
            
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                ViewGroup parentLayout = (ViewGroup) menuDropdown.getParent();
                if (parentLayout != null) {
                    parentLayout.removeView(menuDropdown);
                }
                menuDropdown = null;
            }
            
            @Override
            public void onAnimationCancel(android.animation.Animator animation) {}
            
            @Override
            public void onAnimationRepeat(android.animation.Animator animation) {}
        });
        animator.start();
        
        // Update menu button icon
        updateMenuButtonIcon();
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

        tripsItem.setOnClickListener(v -> {
            onMenuItemSelected("Trips");
            closeMenu();
        });

        historyItem.setOnClickListener(v -> {
            onMenuItemSelected("History");
            closeMenu();
        });

        reportsItem.setOnClickListener(v -> {
            onMenuItemSelected("Reports");
            closeMenu();
        });

        profileItem.setOnClickListener(v -> {
            onMenuItemSelected("Profile");
            closeMenu();
        });

        loginItem.setOnClickListener(v -> {
            onMenuItemSelected("Login");
            closeMenu();
        });

        registerItem.setOnClickListener(v -> {
            onMenuItemSelected("Register");
            closeMenu();
        });
    }

    private void onMenuItemSelected(String itemName) {
        // Handle navigation based on selected menu item
        // This can be extended to navigate to different screens/fragments
        switch (itemName) {
            case "Trips":
                // Navigate to Trips screen
                break;
            case "History":
                // Navigate to History screen
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
