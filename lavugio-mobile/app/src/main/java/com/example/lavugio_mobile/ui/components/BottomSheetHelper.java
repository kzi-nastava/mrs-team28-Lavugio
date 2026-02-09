package com.example.lavugio_mobile.ui.components;

import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class BottomSheetHelper {
    public interface BottomSheetCallback {
        void onStateChanged(int newState);
        void onSlide(float slideOffset);
    }

    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;
    private BottomSheetCallback callback;

    public BottomSheetHelper(FrameLayout bottomSheet, BottomSheetCallback callback) {
        this.callback = callback;
        setupBottomSheet(bottomSheet);
    }

    private void setupBottomSheet(FrameLayout bottomSheet) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setFitToContents(false);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setSkipCollapsed(false);

        // Set peek height (collapsed height)
        bottomSheetBehavior.setPeekHeight(200); // Adjust as needed

        // Start collapsed
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (callback != null) {
                    callback.onStateChanged(newState);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (callback != null) {
                    callback.onSlide(slideOffset);
                }
            }
        });
    }

    public void expand() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void collapse() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public boolean isExpanded() {
        return bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
    }

    public void setPeekHeight(int height) {
        bottomSheetBehavior.setPeekHeight(height);
    }
}
