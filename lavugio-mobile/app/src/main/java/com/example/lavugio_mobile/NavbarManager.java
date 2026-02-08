package com.example.lavugio_mobile;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * NavbarManager is a utility class for managing navbar interactions across activities.
 * It provides reusable methods for toggling the menu and handling navbar item clicks.
 * 
 * This follows the Single Responsibility Principle and promotes code reusability.
 */
public class NavbarManager {

    private final Activity activity;
    private LinearLayout navbarContainer;
    private LinearLayout mobileMenu;
    private ImageView menuButton;
    private boolean isMenuOpen = false;
    private NavbarItemClickListener clickListener;

    /**
     * Interface for handling navbar item click events.
     */
    public interface NavbarItemClickListener {
        void onNavbarItemClicked(String itemId);
    }

    /**
     * Constructor for NavbarManager.
     * 
     * @param activity The activity to manage navbar for
     * @param clickListener The listener for navbar item clicks
     */
    public NavbarManager(Activity activity, NavbarItemClickListener clickListener) {
        this.activity = activity;
        this.clickListener = clickListener;
    }

    /**
     * Initializes the navbar in an activity.
     */
    public void initializeNavbar() {
        // Try to find existing navbar container
        navbarContainer = activity.findViewById(R.id.navbar);
        
        if (navbarContainer == null) {
            // If not found, add navbar dynamically
            addNavbarToActivity();
        } else {
            setupNavbarListeners();
        }
    }

    /**
     * Adds navbar dynamically to the activity's root layout.
     */
    private void addNavbarToActivity() {
        ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView()
                .findViewById(android.R.id.content);
        if (rootView != null) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            View navbar = inflater.inflate(R.layout.navbar, rootView, false);
            rootView.addView(navbar, 0);
            navbarContainer = navbar.findViewById(R.id.navbar);
            setupNavbarListeners();
        }
    }

    /**
     * Sets up click listeners for all navbar items.
     */
    private void setupNavbarListeners() {
        menuButton = navbarContainer.findViewById(R.id.navbar_menu_button);
        mobileMenu = activity.findViewById(R.id.navbar_menu);

        if (menuButton != null) {
            menuButton.setOnClickListener(v -> toggleMenu());
        }

        setupItemListener(R.id.nav_item_trips, "trips");
        setupItemListener(R.id.nav_item_history, "history");
        setupItemListener(R.id.nav_item_reports, "reports");
        setupItemListener(R.id.nav_item_profile, "profile");
        setupItemListener(R.id.nav_item_login, "login");
        setupItemListener(R.id.nav_item_register, "register");
    }

    /**
     * Sets up a click listener for a specific navbar item.
     * 
     * @param viewId The resource ID of the navbar item
     * @param itemId The identifier string for the item
     */
    private void setupItemListener(int viewId, String itemId) {
        View item = navbarContainer.findViewById(viewId);
        if (item != null) {
            item.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onNavbarItemClicked(itemId);
                }
                closeMenu();
            });
        }
    }

    /**
     * Toggles the mobile menu visibility.
     */
    public void toggleMenu() {
        if (mobileMenu != null) {
            isMenuOpen = !isMenuOpen;
            mobileMenu.setVisibility(isMenuOpen ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Closes the mobile menu.
     */
    public void closeMenu() {
        if (isMenuOpen) {
            isMenuOpen = false;
            if (mobileMenu != null) {
                mobileMenu.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Gets the navbar container view.
     * 
     * @return The navbar container
     */
    public LinearLayout getNavbarContainer() {
        return navbarContainer;
    }

    /**
     * Checks if the mobile menu is currently open.
     * 
     * @return true if menu is open, false otherwise
     */
    public boolean isMenuOpen() {
        return isMenuOpen;
    }
}
