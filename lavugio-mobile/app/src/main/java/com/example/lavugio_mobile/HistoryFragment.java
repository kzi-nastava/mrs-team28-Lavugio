package com.example.lavugio_mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.ui.user.history.UserRideHistoryFragment;

/**
 * HistoryFragment displays the trip history for the user.
 * This fragment acts as a container that loads the actual UserRideHistoryFragment.
 */
public class HistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Load the UserRideHistoryFragment
        UserRideHistoryFragment historyFragment = UserRideHistoryFragment.newInstance();

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.history_container, historyFragment)
                .commit();

        // Return the container layout
        return inflater.inflate(R.layout.fragment_history_container, container, false);
    }
}
