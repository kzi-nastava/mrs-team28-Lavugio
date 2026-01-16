package com.example.lavugio_mobile.ui.profile.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lavugio_mobile.R;

public class ProfileHeaderView extends LinearLayout {
    private ImageView profileImage;
    private TextView profileName;
    private TextView profileUserType;
    private TextView profileEmail;

    public ProfileHeaderView(Context context) {
        super(context);
        init(context);
    }

    public ProfileHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProfileHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.profile_header, this, true);

        // Find views
        profileImage = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_name);
        profileUserType = findViewById(R.id.profile_user_type);
        profileEmail = findViewById(R.id.profile_email);
    }

    // Setter methods for updating the view
    public void setProfileImage(int resourceId) {
        profileImage.setImageResource(resourceId);
    }

    public void setProfileImageUrl(String url) {
        // For now, just use placeholder
        // Later you can use Glide or Picasso to load from URL
        // Example: Glide.with(getContext()).load(url).into(profileImage);
        profileImage.setImageResource(R.drawable.ic_launcher_foreground);
    }

    public void setName(String name) {
        profileName.setText(name);
        adjustTextSize(name, profileName);
    }

    private void adjustTextSize(String text, TextView textView) {
        int length = text.length();

        if (length <= 15) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        } else if (length <= 20) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        } else {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }
    }

    public void setUserType(String userType) {
        profileUserType.setText(userType);
    }

    public void setEmail(String email) {
        profileEmail.setText(email);
    }

    // Convenience method to set all data at once
    public void setProfileData(String name, String userType, String email) {
        setName(name);
        setUserType(userType);
        setEmail(email);
    }
}