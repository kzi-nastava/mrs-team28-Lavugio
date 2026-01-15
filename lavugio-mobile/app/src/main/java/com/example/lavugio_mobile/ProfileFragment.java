package com.example.lavugio_mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

/**
 * ProfileFragment displays the user's profile information.
 */
public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create a simple container for the fragment
        TextView view = new TextView(getActivity());
        view.setText("Profile Fragment");
        view.setTextSize(18);
        view.setPadding(16, 16, 16, 16);
        return view;
    }
}
