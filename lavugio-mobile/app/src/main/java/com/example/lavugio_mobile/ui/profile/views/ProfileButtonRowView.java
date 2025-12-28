package com.example.lavugio_mobile.ui.profile.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.example.lavugio_mobile.R;

public class ProfileButtonRowView extends LinearLayout {
    private Button leftButton;
    private Button rightButton;
    private OnButtonClickListener listener;

    public interface OnButtonClickListener {
        void onLeftButtonClick();
        void onRightButtonClick();
    }

    public ProfileButtonRowView(Context context) {
        super(context);
        init(context);
    }

    public ProfileButtonRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProfileButtonRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        // Create left button (Activate/Deactivate for drivers)
        leftButton = createStyledButton("Activate", R.color.lavugio_dark_orange);

        // Create right button (Edit for everyone)
        rightButton = createStyledButton("Edit", R.color.lavugio_dark_green);

        // Set layout params with spacing
        LayoutParams leftParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
        leftParams.setMargins(0, 0, dpToPx(8), 0);
        leftButton.setLayoutParams(leftParams);

        LayoutParams rightParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
        rightParams.setMargins(dpToPx(8), 0, 0, 0);
        rightButton.setLayoutParams(rightParams);

        // Set click listeners
        leftButton.setOnClickListener(v -> {
            if (listener != null) listener.onLeftButtonClick();
        });

        rightButton.setOnClickListener(v -> {
            if (listener != null) listener.onRightButtonClick();
        });

        addView(leftButton);
        addView(rightButton);
    }

    private Button createStyledButton(String text, int colorResId) {
        Button button = new Button(getContext());
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        button.setAllCaps(false);
        button.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));

        // Create rounded background with color from resources
        GradientDrawable background = new GradientDrawable();
        background.setColor(ContextCompat.getColor(getContext(), colorResId));
        background.setCornerRadius(dpToPx(8));
        button.setBackground(background);

        return button;
    }

    // Public methods to configure the view

    public void setLeftButtonVisible(boolean visible) {
        leftButton.setVisibility(visible ? VISIBLE : GONE);

        // Adjust right button layout when left button is hidden
        LayoutParams rightParams = (LayoutParams) rightButton.getLayoutParams();
        if (visible) {
            rightParams.weight = 1;
            rightParams.setMargins(dpToPx(8), 0, 0, 0);
        } else {
            rightParams.weight = 0;
            rightParams.width = LayoutParams.WRAP_CONTENT;
            rightParams.setMargins(0, 0, 0, 0);
        }
        rightButton.setLayoutParams(rightParams);
    }

    public void setLeftButtonEnabled(boolean enabled) {
        leftButton.setEnabled(enabled);

        // Visual feedback for disabled state
        if (enabled) {
            leftButton.setAlpha(1.0f);
        } else {
            leftButton.setAlpha(0.5f);
        }
    }

    public void setLeftButtonText(String text) {
        leftButton.setText(text);
    }

    public void setRightButtonText(String text) {
        rightButton.setText(text);
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.listener = listener;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}