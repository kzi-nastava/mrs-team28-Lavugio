package com.example.lavugio_mobile.ui.ride;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;

public class FindRidePage2Fragment extends Fragment {
    private static final String TAG = "FindRidePage2";
    private Button btnPrevious, btnNext;

    private View viewPetCheckbox, viewBabyCheckbox;
    private LinearLayout llPetFriendly, llBabyFriendly;
    private boolean isPetFriendly = false;
    private boolean isBabyFriendly = false;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_ride_page_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);
        viewPetCheckbox = view.findViewById(R.id.viewPetCheckbox);
        viewBabyCheckbox = view.findViewById(R.id.viewBabyCheckbox);
        llPetFriendly = view.findViewById(R.id.llPetFriendly);
        llBabyFriendly = view.findViewById(R.id.llBabyFriendly);
        setupButtons();
    }

    private void setupButtons() {
        btnPrevious.setOnClickListener(v -> {
            ((FindRideFragment) getParentFragment()).previousPage();
        });

        btnNext.setOnClickListener(v -> {
            ((FindRideFragment) getParentFragment()).nextPage();
        });

        llPetFriendly.setOnClickListener(v -> {
            isPetFriendly = !isPetFriendly;
            viewPetCheckbox.setBackgroundResource(isPetFriendly ?
                    R.drawable.checkbox_checked : R.drawable.checkbox_unchecked);
        });

        llBabyFriendly.setOnClickListener(v -> {
            isBabyFriendly = !isBabyFriendly;
            viewBabyCheckbox.setBackgroundResource(isBabyFriendly ?
                    R.drawable.checkbox_checked : R.drawable.checkbox_unchecked);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
