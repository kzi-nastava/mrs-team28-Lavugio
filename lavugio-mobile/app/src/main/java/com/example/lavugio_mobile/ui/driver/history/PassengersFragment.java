package com.example.lavugio_mobile.ui.driver.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lavugio_mobile.BuildConfig;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.PassengerTableRow;

import java.util.List;

public class PassengersFragment extends Fragment {

    private RecyclerView recyclerPassengers;
    private PassengerAdapter adapter;
    private List<PassengerTableRow> passengers;
    private final String backendUrl = "http://" + BuildConfig.SERVER_IP + ":8080/";

    public static PassengersFragment newInstance() {
        return new PassengersFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passengers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerPassengers = view.findViewById(R.id.recyclerPassengers);
        recyclerPassengers.setLayoutManager(new LinearLayoutManager(requireContext()));

        if (passengers != null) {
            adapter = new PassengerAdapter(passengers, backendUrl);
            recyclerPassengers.setAdapter(adapter);
        }
    }

    public void updatePassengers(List<PassengerTableRow> passengers) {
        this.passengers = passengers;
        if (recyclerPassengers != null) {
            if (adapter == null) {
                adapter = new PassengerAdapter(passengers, backendUrl);
                recyclerPassengers.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }
}
