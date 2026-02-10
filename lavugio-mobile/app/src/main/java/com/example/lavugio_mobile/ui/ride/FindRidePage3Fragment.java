package com.example.lavugio_mobile.ui.ride;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;

public class FindRidePage3Fragment extends Fragment {
    private static final String TAG = "FindRidePage3";
    private Button btnPrevious, btnFinish;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_ride_page_3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnFinish = view.findViewById(R.id.btnFinish);

        setupButtons();
    }

    private void setupButtons() {
        btnPrevious.setOnClickListener(v -> {
            ((FindRideFragment) getParentFragment()).previousPage();
        });

        btnFinish.setOnClickListener(v -> {
            orderRide();
        });
    }

    private void orderRide() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
