package com.example.lavugio_mobile.ui.driver.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.RideHistoryDriverDetailedModel;

public class RideInfoFragment extends Fragment {

    private static final String ARG_RIDE = "ride";

    private RideHistoryDriverDetailedModel ride;

    private TextView startText;
    private TextView endText;
    private TextView departureText;
    private TextView destinationText;
    private TextView priceText;

    private ImageView cancelledIcon;
    private ImageView panicIcon;

    public static RideInfoFragment newInstance() {
        return new RideInfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_ride_history_detailed_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startText = view.findViewById(R.id.tvBegin);
        endText = view.findViewById(R.id.tvEnd);
        departureText = view.findViewById(R.id.tvDeparture);
        destinationText = view.findViewById(R.id.tvDestination);
        priceText = view.findViewById(R.id.tvPrice);
        cancelledIcon = view.findViewById(R.id.imgCancelledStatus);
        panicIcon = view.findViewById(R.id.imgPanicStatus);

        if (ride != null) {
            updateUI();
        }
    }

    public void updateInfo(RideHistoryDriverDetailedModel ride) {
        this.ride = ride;
        if (getView() != null) {
            updateUI();
        }
    }

    private void updateUI() {
        startText.setText(ride.getStart());
        endText.setText(ride.getEnd());
        departureText.setText(ride.getDeparture());
        destinationText.setText(ride.getDestination());
        priceText.setText(ride.getPrice() + " RSD");

        // Set icons
        if (ride.isCancelled()) {
            cancelledIcon.setImageResource(R.drawable.ic_check_red);
        } else {
            cancelledIcon.setImageResource(R.drawable.ic_cross_green);
        }

        if (ride.isPanic()) {
            panicIcon.setImageResource(R.drawable.ic_check_red);
        } else {
            panicIcon.setImageResource(R.drawable.ic_cross_green);
        }
    }
}
