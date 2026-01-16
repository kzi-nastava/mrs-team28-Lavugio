package com.example.lavugio_mobile;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * BaseActivity provides a common navbar interface for all activities in the application.
 * This follows the DRY principle and ensures consistent navigation across all screens.
 * 
 * Activities should extend this class to automatically include the navbar UI.
 * 
 * Example:
 * public class MyActivity extends BaseActivity {
 *     @Override
 *     protected void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *         setContentView(R.layout.activity_main);
 *         initializeNavbar();
 *     }
 * }
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected LinearLayout navbarContainer;
    protected LinearLayout mobileMenu;
    protected ImageView menuButton;
    protected boolean isMenuOpen = false;

    /**
     * Initializes the navbar and sets up navigation item click listeners.
     * Call this method in your activity's onCreate after calling setContentView.
     */
    protected void initializeNavbar() {
        // Get navbar container from the activity's root layout
        navbarContainer = findViewById(R.id.navbar_container);
        if (navbarContainer == null) {
            // If no navbar_container exists in the current layout, add navbar dynamically
            addNavbarToLayout();
        } else {
            setupNavbarListeners();
        }
    }

    /**
     * Adds the navbar dynamically to the activity layout if it doesn't exist.
     * This is useful for activities with custom layouts.
     */
    private void addNavbarToLayout() {
        // Get the root view of the activity
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView instanceof LinearLayout) {
            LinearLayout rootLayout = (LinearLayout) rootView;
            LinearLayout.LayoutParams navbarParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            
            // Inflate and add navbar at the top
            View navbar = getLayoutInflater().inflate(R.layout.navbar, null);
            rootLayout.addView(navbar, 0, navbarParams);
            navbarContainer = navbar.findViewById(R.id.navbar_container);
            setupNavbarListeners();
        }
    }

    /**
     * Sets up click listeners for navbar items and menu button.
     */
    private void setupNavbarListeners() {
        // Menu button
        menuButton = navbarContainer.findViewById(R.id.navbar_menu_btn);
        mobileMenu = navbarContainer.findViewById(R.id.navbar_mobile_menu);

        if (menuButton != null) {
            menuButton.setOnClickListener(v -> toggleMobileMenu());
        }

        // Menu items
        if (navbarContainer.findViewById(R.id.navbar_item_trips) != null) {
            navbarContainer.findViewById(R.id.navbar_item_trips).setOnClickListener(
                    v -> onNavbarItemClicked("trips")
            );
        }
        if (navbarContainer.findViewById(R.id.navbar_item_history) != null) {
            navbarContainer.findViewById(R.id.navbar_item_history).setOnClickListener(
                    v -> onNavbarItemClicked("history")
            );
        }
        if (navbarContainer.findViewById(R.id.navbar_item_reports) != null) {
            navbarContainer.findViewById(R.id.navbar_item_reports).setOnClickListener(
                    v -> onNavbarItemClicked("reports")
            );
        }
        if (navbarContainer.findViewById(R.id.navbar_item_profile) != null) {
            navbarContainer.findViewById(R.id.navbar_item_profile).setOnClickListener(
                    v -> onNavbarItemClicked("profile")
            );
        }
        if (navbarContainer.findViewById(R.id.navbar_item_login) != null) {
            navbarContainer.findViewById(R.id.navbar_item_login).setOnClickListener(
                    v -> onNavbarItemClicked("login")
            );
        }
        if (navbarContainer.findViewById(R.id.navbar_item_register) != null) {
            navbarContainer.findViewById(R.id.navbar_item_register).setOnClickListener(
                    v -> onNavbarItemClicked("register")
            );
        }
    }

    /**
     * Toggles the visibility of the mobile menu.
     */
    private void toggleMobileMenu() {
        if (mobileMenu != null) {
            isMenuOpen = !isMenuOpen;
            mobileMenu.setVisibility(isMenuOpen ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Closes the mobile menu.
     */
    protected void closeMobileMenu() {
        if (mobileMenu != null && isMenuOpen) {
            isMenuOpen = false;
            mobileMenu.setVisibility(View.GONE);
        }
    }

    /**
     * Called when a navbar item is clicked.
     * Override this method in subclasses to handle navigation.
     * 
     * @param itemId The identifier of the clicked navbar item
     */
    protected void onNavbarItemClicked(String itemId) {
        closeMobileMenu();
        // Default implementation - override in subclasses for specific behavior
    }

    /**
     * Close menu when back is pressed if menu is open.
     */
    @Override
    public void onBackPressed() {
        if (isMenuOpen) {
            closeMobileMenu();
        } else {
            super.onBackPressed();
        }
    }
}
