package com.example.lavugio_mobile;

import android.os.Bundle;
import android.widget.TextView;

/**
 * SampleActivity demonstrates how to extend BaseActivity to include navbar.
 * 
 * This is an example for creating new activities that should have the navbar visible.
 * Follow this pattern for any new activities you create.
 */
public class SampleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set your activity's layout
        setContentView(R.layout.activity_main);  // Replace with your actual layout
        
        // Initialize navbar - IMPORTANT: must call this after setContentView()
        initializeNavbar();
    }

    /**
     * Called when a navbar item is clicked.
     * Override this method to handle navigation based on the clicked item.
     * 
     * @param itemId The identifier of the clicked navbar item
     */
    @Override
    protected void onNavbarItemClicked(String itemId) {
        // First, call super implementation to handle menu closing
        super.onNavbarItemClicked(itemId);
        
        // Then handle specific navigation logic
        switch (itemId) {
            case "trips":
                // Navigate to trips or load trips fragment
                handleTripsClick();
                break;
                
            case "history":
                // Navigate to history or load history fragment
                handleHistoryClick();
                break;
                
            case "reports":
                // Navigate to reports or load reports fragment
                handleReportsClick();
                break;
                
            case "profile":
                // Navigate to profile or load profile fragment
                handleProfileClick();
                break;
                
            case "login":
                // Navigate to login activity
                handleLoginClick();
                break;
                
            case "register":
                // Navigate to register activity
                handleRegisterClick();
                break;
                
            default:
                break;
        }
    }

    // Navigation handlers - implement based on your application's navigation pattern

    private void handleTripsClick() {
        // TODO: Implement trips navigation
    }

    private void handleHistoryClick() {
        // TODO: Implement history navigation
    }

    private void handleReportsClick() {
        // TODO: Implement reports navigation
    }

    private void handleProfileClick() {
        // TODO: Implement profile navigation
    }

    private void handleLoginClick() {
        // TODO: Implement login navigation
    }

    private void handleRegisterClick() {
        // TODO: Implement register navigation
    }
}
