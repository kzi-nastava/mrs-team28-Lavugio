package com.example.lavugio_mobile.ui.ride;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;

public class FindTripPage1Fragment extends Fragment {
    private EditText etDestination;
    private AppCompatImageButton btnAddDestination;
    private TextView tvNoDestinations;
    private EditText etFavoriteRoute;
    private AppCompatImageButton btnSaveFavorite;
    private Button btnPrevious, btnNext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_trip_page_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etDestination = view.findViewById(R.id.etDestination);
        btnAddDestination = view.findViewById(R.id.btnAddDestination);
        tvNoDestinations = view.findViewById(R.id.tvNoDestinations);
        etFavoriteRoute = view.findViewById(R.id.etFavoriteRoute);
        btnSaveFavorite = view.findViewById(R.id.btnSaveFavorite);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);

        btnAddDestination.setOnClickListener(v -> {
            // Add destination logic
        });

        btnSaveFavorite.setOnClickListener(v -> {
            // Save favorite route logic
        });

        btnPrevious.setVisibility(View.GONE); // Hide on first page

        btnNext.setOnClickListener(v -> {
            ((FindRideFragment) getParentFragment()).nextPage();
        });
    }
}
